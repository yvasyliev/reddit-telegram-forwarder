package com.github.yvasyliev.model.events;

import com.github.yvasyliev.model.dto.ChannelPost;
import org.springframework.context.ApplicationEvent;

public class NewChannelPostEvent extends ApplicationEvent {
    public NewChannelPostEvent(ChannelPost channelPost) {
        super(channelPost);
    }

    @Override
    public ChannelPost getSource() {
        return (ChannelPost) super.getSource();
    }
}
