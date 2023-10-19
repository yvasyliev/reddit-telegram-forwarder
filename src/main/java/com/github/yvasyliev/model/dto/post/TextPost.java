package com.github.yvasyliev.model.dto.post;

public class TextPost extends Post {
    @Override
    public String getType() {
        return Type.TEXT;
    }
}
