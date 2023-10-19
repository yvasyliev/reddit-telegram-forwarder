package com.github.yvasyliev.model.dto.post;

public class VideoPost extends MediaPost {
    @Override
    public String getType() {
        return Type.VIDEO;
    }
}
