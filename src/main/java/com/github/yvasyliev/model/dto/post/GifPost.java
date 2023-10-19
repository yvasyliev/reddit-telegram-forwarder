package com.github.yvasyliev.model.dto.post;

public class GifPost extends MediaPost {
    @Override
    public String getType() {
        return Type.GIF;
    }
}
