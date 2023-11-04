package com.github.yvasyliev.model.events;

import com.github.yvasyliev.model.dto.ChannelPost;
import org.springframework.context.ApplicationEvent;

public class NewChannelPost extends ApplicationEvent {
    public NewChannelPost(ChannelPost channelPost) {
        super(channelPost);
    }
}
