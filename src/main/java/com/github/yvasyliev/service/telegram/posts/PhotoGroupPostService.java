package com.github.yvasyliev.service.telegram.posts;

import com.github.yvasyliev.model.dto.post.PhotoGroupPost;
import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.model.events.NewChannelPostEvent;
import com.github.yvasyliev.service.telegram.readers.BotResponseReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@Service(Post.Type.PHOTO_GROUP)
public class PhotoGroupPostService extends PostService<PhotoGroupPost, List<Message>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoGroupPostService.class);

    @Autowired
    private BotResponseReader responseReader;

    @Autowired
    @Qualifier("synchronizedFixedSizeMap")
    private Map<Integer, PhotoGroupPost> extraPhotos;

    @Autowired
    private Function<String, String> markdownV2Escaper;

    @Override
    @NonNull
    public Optional<List<Message>> applyWithException(@NonNull String chatId, PhotoGroupPost post) throws TelegramApiException, URISyntaxException, IOException, ExecutionException, InterruptedException {
        var pages = post.getPhotoUrlsPages();
        var text = markdownV2Escaper.apply(post.getText());
        var hasSpoiler = post.isHasSpoiler();

        var pagesAmount = pages.size();
        var caption = pagesAmount > 1
                ? responseReader.applyWithException("responses/photogroup.md").formatted(text)
                : text;

        var sendMediaGroup = sendMediaGroup(chatId, pages.element(), hasSpoiler, null);
        sendMediaGroup.getMedias().get(0).setCaption(caption);

        var publishedPost = redditTelegramForwarderBot.executeDelayed(sendMediaGroup).get();
        var messages = new ArrayList<>(publishedPost);
        var messageId = publishedPost.get(0).getMessageId();

        if (!post.isApproved()) {
            messages.addAll(sendDelayed(chatId, messageId, post));
        } else {
            extraPhotos.put(messageId, post);
        }

        return Optional.of(messages);
    }

    private SendMediaGroup sendMediaGroup(String chatId, Queue<String> page, boolean hasSpoiler, Integer replyToMessageId) {
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

    private List<Message> sendDelayed(String chatId, int replyToMessageId, PhotoGroupPost post) throws ExecutionException, InterruptedException {
        var pages = new ArrayDeque<>(post.getPhotoUrlsPages());
        var hasSpoiler = post.isHasSpoiler();
        var messages = new ArrayList<Message>();
        pages.removeFirst();
        for (var page : pages) {
            messages.addAll(page.size() > 1
                    ? redditTelegramForwarderBot.executeDelayed(sendMediaGroup(chatId, page, hasSpoiler, replyToMessageId)).get()
                    : List.of(redditTelegramForwarderBot.executeDelayed(sendPhoto(chatId, page.element(), hasSpoiler, replyToMessageId)).get())
            );
        }
        return messages;
    }

    private SendPhoto sendPhoto(String chatId, String photo, boolean hasSpoiler, Integer replyToMessageId) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(photo))
                .hasSpoiler(hasSpoiler)
                .replyToMessageId(replyToMessageId)
                .build();
    }

    @EventListener
    public void onNewChannelPostEvent(NewChannelPostEvent newChannelPostEvent) {
        var channelPost = newChannelPostEvent.getChannelPost();
        Optional
                .ofNullable(extraPhotos.remove(channelPost.forwardFromMessageId()))
                .ifPresent(post -> {
                    try {
                        sendDelayed(
                                redditTelegramForwarderBot.getChatId(),
                                channelPost.messageId(),
                                post
                        );
                    } catch (ExecutionException | InterruptedException e) {
                        LOGGER.error("Failed to send extra photos.", e);
                    }
                });
    }
}
