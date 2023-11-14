package com.github.yvasyliev.model.dto.post;

public class TextPost extends Post {
    @Override
    public String getType() {
        return Type.TEXT;
    }

    @Override
    public String toString() {
        return "TextPost{} " + super.toString();
    }
}
