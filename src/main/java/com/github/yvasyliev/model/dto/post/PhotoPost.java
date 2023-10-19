package com.github.yvasyliev.model.dto.post;

public class PhotoPost extends MediaPost {
    @Override
    public String getType() {
        return Type.PHOTO;
    }
}
