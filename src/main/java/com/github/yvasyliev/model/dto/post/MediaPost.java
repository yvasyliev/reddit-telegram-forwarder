package com.github.yvasyliev.model.dto.post;

public abstract class MediaPost extends SpoilerablePost {
    private String mediaUrl;

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }
}
