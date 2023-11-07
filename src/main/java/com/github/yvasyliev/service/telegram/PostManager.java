package com.github.yvasyliev.service.telegram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.bots.telegram.RedTelBot;
import com.github.yvasyliev.model.dto.RedditPostDecisionData;
import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.model.events.NewChannelPostEvent;
import com.github.yvasyliev.service.data.RedditTelegramForwarderPropertyService;
import com.github.yvasyliev.service.reddit.SubredditPostSupplier;
import com.github.yvasyliev.service.telegram.posts.PhotoGroupPostService;
import com.github.yvasyliev.service.telegram.posts.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class PostManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostManager.class);

    @Autowired
    private ScheduledExecutorService executorService;

    @Autowired
    private SubredditPostSupplier subredditPostSupplier;

    @Autowired
    private RedTelBot redTelBot;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Map<Integer, Post> postCandidates;

    @Autowired
    private RedditTelegramForwarderPropertyService propertyService;

    @Autowired
    private PhotoGroupPostService photoGroupPostService;

    private final AtomicBoolean publishing = new AtomicBoolean(true);

    public void schedulePosting() {
        executorService.scheduleWithFixedDelay(() -> {
            if (publishing.get()) {
                try {
                    var newPosts = subredditPostSupplier.getWithException();
                    publishPosts(newPosts);
                } catch (Exception e) {
                    LOGGER.error("Failed to find new posts.", e);
                }
            }
        }, 0, 1, TimeUnit.MINUTES);
        LOGGER.info("Started posting.");
    }

    public void publishPosts(List<Post> posts) {
        posts.forEach(this::publishPost);
    }

    public <T extends Post> void publishPost(T post) {
        if (context.containsBean(post.getType())) {
            try {
                publishPost(post, post.getType());
            } catch (TelegramApiException | JsonProcessingException e) {
                LOGGER.error("Failed to ask approve.", e);
            } catch (Exception e) {
                LOGGER.error("Failed to send post: {}", post, e);
            }
            propertyService.saveLastCreated(post.getCreated());
        }
    }

    public <T extends Post> void publishPost(T post, String postServiceName) throws Exception {
        @SuppressWarnings("unchecked") var postService = (PostService<T, ?>) context.getBean(postServiceName);
        var chatId = post.isApproved() ? redTelBot.getChannelId() : redTelBot.getAdminId();
        var sentMessage = postService.applyWithException(chatId, post);
        if (sentMessage.isPresent() && !post.isApproved()) {
            askApprove(chatId, post.getCreated());
            postCandidates.put(post.getCreated(), post);
        }
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

    @EventListener
    public void onNewChannelPostEvent(NewChannelPostEvent newChannelPostEvent) {
        var channelPost = newChannelPostEvent.getChannelPost();
        try {
            photoGroupPostService.sendExtraPhotos(channelPost.messageId(), channelPost.forwardFromMessageId());
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send extra photos.", e);
        }
    }

    public void publishPostCandidate(int created) {
        var post = rejectPostCandidate(created);
        post.setApproved(true);
        publishPost(post);
    }

    public Post rejectPostCandidate(int created) {
        return postCandidates.remove(created);
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
        return redTelBot.execute(sendMessage);
    }
}
