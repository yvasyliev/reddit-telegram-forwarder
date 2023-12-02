package com.github.yvasyliev.service.telegram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.bots.telegram.RedditTelegramForwarderBot;
import com.github.yvasyliev.model.dto.RedditPostDecisionData;
import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.service.data.RedditTelegramForwarderPropertyService;
import com.github.yvasyliev.service.telegram.posts.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PostManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostManager.class);

    @Autowired
    private RedditTelegramForwarderBot redditTelegramForwarderBot;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("synchronizedFixedSizeMap")
    private Map<Integer, Post> postCandidates;

    @Autowired
    private RedditTelegramForwarderPropertyService propertyService;

    public void publishPosts(List<Post> posts) {
        posts.forEach(this::publishPost);
    }

    public void publishPost(Post post) {
        if (context.containsBean(post.getType())) {
            try {
                publishPost(post, post.getType());
            } catch (Exception e) {
                LOGGER.error("Failed to send post: {}", post, e);
            }
            propertyService.saveLastCreated(post.getCreated());
        }
    }

    public <T extends Post> void publishPost(T post, String postServiceName) throws Exception {
        @SuppressWarnings("unchecked") var postService = (PostService<T, ?>) context.getBean(postServiceName);
        var chatId = post.isApproved() ? redditTelegramForwarderBot.getChannelId() : redditTelegramForwarderBot.getAdminId();
        var sentMessage = postService.applyWithException(chatId, post);
        if (sentMessage.isPresent() && !post.isApproved()) {
            try {
                askApprove(chatId, post.getCreated());
                postCandidates.put(post.getCreated(), post);
            } catch (TelegramApiException | JsonProcessingException e) {
                LOGGER.error("Failed to ask approve.", e);
            }
        }
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
        return redditTelegramForwarderBot.execute(sendMessage);
    }

    public void publishPostCandidate(int created) {
        Optional
                .ofNullable(rejectPostCandidate(created))
                .ifPresent(post -> {
                    post.setApproved(true);
                    publishPost(post);
                });
    }

    public Post rejectPostCandidate(int created) {
        return postCandidates.remove(created);
    }
}
