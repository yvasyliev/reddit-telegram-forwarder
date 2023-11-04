package com.github.yvasyliev.service.telegram;

import com.github.yvasyliev.model.dto.post.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ChatIdFactory implements Function<Post, String> {
    @Value("${telegram.admin.id}")
    private String adminId;

    @Value("${telegram.channel.id}")
    private String channelId;

    @Override
    public String apply(Post post) {
        return post.isApproved() ? channelId : adminId;
    }
}
