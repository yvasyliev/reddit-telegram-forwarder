package com.github.yvasyliev.bots.telegram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.model.dto.Post;
import com.github.yvasyliev.model.dto.RedditPostDecisionData;
import com.github.yvasyliev.service.state.StateManager;
import com.github.yvasyliev.service.reddit.RedditPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.function.ThrowingBiFunction;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public abstract class TelegramPublisher extends AbstractRedTelBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedditPostService.class);
    @Autowired
    private ScheduledExecutorService executorService;

    @Autowired
    private RedditPostService redditPostService;

    private final AtomicBoolean publishing = new AtomicBoolean(true);

    @Autowired
    private StateManager stateManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Map<Integer, Post> postCandidates;

    @Autowired
    private Map<Integer, Post> extraPhotos;

    public TelegramPublisher(String botToken) {
        super(botToken);
    }

    public void pausePublishing() {
        publishing.set(false);
    }

    public void resumePublishing() {
        publishing.set(true);
    }

    public void stopPublishing() {
        executorService.shutdown();
    }

    public void startPublishingPosts() {
        executorService.scheduleWithFixedDelay(() -> {
            if (publishing.get()) {
                try {
                    var newPosts = redditPostService.findNewPosts();
                    publishPosts(newPosts);
                } catch (Exception e) {
                    LOGGER.error("Failed to find new posts.", e);
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    public void publishPosts(List<Post> posts) {
        posts = new ArrayList<>(posts);
//        posts.removeIf(post -> 1696321369 < post.getCreated());
        posts.forEach(this::publishPost);
    }

    public void publishPost(Post post) {
        var chatId = getChatId(post.isApproved());
        var created = post.getCreated();
        try {
            var sentMessage = switch (post.getType()) {
                case TEXT -> execute(new SendMessage(chatId, post.getText()));
                case PHOTO -> sendPhotoPost(chatId, post);
                case PHOTO_GROUP -> sendPhotoGroup(chatId, post);
                case GIF -> sendGif(chatId, post);
                case VIDEO -> sendVideo(chatId, post);
                case POLL -> sendPoll(chatId, post);
            };
            if (sentMessage != null && !post.isApproved()) {
                askApprove(chatId, created);
                postCandidates.put(created, post);
            }
            stateManager.setLastCreated(created);
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send post: {}", post, e);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to serialize post: {}", post, e);
        } catch (IOException e) {
            LOGGER.error("Failed to save last_created: {}", created, e);
        }
//        try {
//            TimeUnit.SECONDS.sleep(10);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }

    private Message sendPoll(String chatId, Post post) throws TelegramApiException {
        var sendPoll = SendPoll.builder()
                .chatId(chatId)
                .question(post.getText())
                .options(post.getOptions())
                .build();

        return execute(sendPoll);
    }

    private String getChatId(boolean approved) {
        return approved ? getChannelId() : getAdminId();
    }

    private Message sendPhotoPost(String chatId, Post post) throws TelegramApiException {
        try {
            var sendPhoto = SendPhoto.builder()
                    .chatId(chatId)
                    .photo(new InputFile(post.getMediaUrl()))
                    .caption(post.getText())
                    .hasSpoiler(post.isHasSpoiler())
                    .build();
            return execute(sendPhoto);
        } catch (TelegramApiRequestException e) {
            return retrySendPhoto(chatId, post, e);
        }
    }

    private Message retrySendPhoto(String chatId, Post post, TelegramApiRequestException requestException) throws TelegramApiException {
        try {
            return onException(
                    requestException,
                    ex -> "Bad Request: wrong file identifier/HTTP URL specified".equals(ex.getApiResponse()),
                    (inputStream, filename) -> {
                        var sendPhoto = SendPhoto.builder()
                                .chatId(chatId)
                                .photo(new InputFile(inputStream, filename))
                                .caption(post.getText())
                                .hasSpoiler(post.isHasSpoiler())
                                .build();
                        return execute(sendPhoto);
                    },
                    post
            );
        } catch (TelegramApiRequestException e) {
            return onException(
                    requestException,
                    ex -> "Bad Request: PHOTO_INVALID_DIMENSIONS".equals(ex.getApiResponse()),
                    (inputStream, filename) -> {
                        var sendPhoto = SendDocument.builder()
                                .chatId(chatId)
                                .document(new InputFile(inputStream, filename))
                                .caption(post.getText())
                                .build();
                        return execute(sendPhoto);
                    },
                    post
            );
        }
    }

    private Message onException(
            TelegramApiRequestException e,
            Predicate<TelegramApiRequestException> exceptionPredicate,
            ThrowingBiFunction<InputStream, String, Message> telegramApiCall,
            Post post
    ) throws TelegramApiException {
        if (exceptionPredicate.negate().test(e)) {
            throw e;
        }

        return sendWithInputStream(telegramApiCall, post);
    }

    private Message sendWithInputStream(ThrowingBiFunction<InputStream, String, Message> telegramApiCall, Post post) throws TelegramApiException {
        var mediaUrl = post.getMediaUrl();
        try (var inputStream = new URL(mediaUrl).openStream()) {
            var filename = mediaUrl.substring(mediaUrl.lastIndexOf('/') + 1);
            if (filename.contains("?")) {
                filename = filename.substring(0, filename.indexOf("?"));
            }
            return telegramApiCall.applyWithException(inputStream, filename);
        } catch (Exception e) {
            throw new TelegramApiException(e);
        }
    }

    private List<Message> sendPhotoGroup(String chatId, Post post) throws TelegramApiException {
        var pages = post.getPhotoUrlsPages();
        var text = post.getText();
        var hasSpoiler = post.isHasSpoiler();

        var pagesAmount = pages.size();
        var caption = pagesAmount > 1
                ? """
                %s
                                    
                *More in comments 👇👇👇*""".formatted(text)
                : text;

        var sendMediaGroup = sendMediaGroup(chatId, pages.get(0), hasSpoiler, null);
        sendMediaGroup.getMedias().get(0).setCaption(caption);

        var publishedPost = execute(sendMediaGroup);
        var messages = new ArrayList<>(publishedPost);
        var messageId = publishedPost.get(0).getMessageId();

        if (!post.isApproved()) {
            for (var i = 1; i < pagesAmount; i++) {
                var page = pages.get(i);
                messages.addAll(page.size() > 1
                        ? executeDelayed(sendMediaGroup(chatId, page, hasSpoiler, messageId))
                        : List.of(executeDelayed(sendPhoto(chatId, page.get(0), hasSpoiler, messageId)))
                );
            }
        } else {
            extraPhotos.put(messageId, post);
        }

        return messages;
    }

    public List<Message> sendExtraPhotos(int replyMessageId, int forwardMessageId) {
        var post = extraPhotos.remove(forwardMessageId);

        if (post == null) {
            return List.of();
        }

        var hasSpoiler = post.isHasSpoiler();
        var pages = post.getPhotoUrlsPages();
        var pagesAmount = pages.size();
        var messages = new ArrayList<Message>();
        for (var i = 1; i < pagesAmount; i++) {
            var page = pages.get(i);
            try {
                messages.addAll(page.size() > 1
                        ? executeDelayed(sendMediaGroup(getGroupId(), page, hasSpoiler, replyMessageId))
                        : List.of(executeDelayed(sendPhoto(getGroupId(), page.get(0), hasSpoiler, replyMessageId)))
                );
            } catch (TelegramApiException e) {
                LOGGER.error("Failed to send extra photos ({}) of {}", i, post, e);
                break;
            }
        }
        return messages;
    }

    private SendMediaGroup sendMediaGroup(String chatId, List<String> page, boolean hasSpoiler, Integer replyToMessageId) {
        var inputMediaPhotos = page
                .stream()
                .map(photoUrl -> (InputMedia) InputMediaPhoto
                        .builder()
                        .media(photoUrl)
                        .hasSpoiler(hasSpoiler)
                        .parseMode(ParseMode.MARKDOWNV2)
                        .build()
                )
                .toList();

        return SendMediaGroup.builder()
                .chatId(chatId)
                .medias(inputMediaPhotos)
                .replyToMessageId(replyToMessageId)
                .build();
    }

    private SendPhoto sendPhoto(String chatId, String photo, boolean hasSpoiler, Integer replyToMessageId) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(photo))
                .hasSpoiler(hasSpoiler)
                .replyToMessageId(replyToMessageId)
                .build();
    }

    private Message sendGif(String chatId, Post post) throws TelegramApiException {
        return sendWithInputStream((inputStream, filename) -> {
            var sendAnimation = SendAnimation.builder()
                    .chatId(chatId)
                    .animation(new InputFile(inputStream, filename))
                    .caption(post.getText())
                    .hasSpoiler(post.isHasSpoiler())
                    .build();

            return execute(sendAnimation);
        }, post);
    }

    private Message sendVideo(String chatId, Post post) throws TelegramApiException {
        return sendWithInputStream((inputStream, filename) -> {
            var sendVideo = SendVideo.builder()
                    .chatId(chatId)
                    .video(new InputFile(inputStream, filename))
                    .caption(post.getText())
                    .hasSpoiler(post.isHasSpoiler())
                    .supportsStreaming(true)
                    .build();

            return execute(sendVideo);
        }, post);
    }

    private Message askApprove(String chatId, int created) throws TelegramApiException, JsonProcessingException {
        var approveButton = InlineKeyboardButton.builder()
                .text("✅ Approve")
                .callbackData(objectMapper.writeValueAsString(new RedditPostDecisionData(
                        "/approveredditpost",
                        created
                )))
                .build();

        var denyButton = InlineKeyboardButton.builder()
                .text("🚫 Reject")
                .callbackData(objectMapper.writeValueAsString(new RedditPostDecisionData(
                        "/rejectredditpost",
                        created
                )))
                .build();

        var sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("👆 Shall I publish the post above?")
                .replyMarkup(new InlineKeyboardMarkup(List.of(List.of(
                        approveButton,
                        denyButton
                ))))
                .build();
        return execute(sendMessage);
    }

    public void publishPostCandidate(int created) {
        var post = rejectPostCandidate(created);
        post.setApproved(true);
        publishPost(post);
    }

    public Post rejectPostCandidate(int created) {
        return postCandidates.remove(created);
    }
}
