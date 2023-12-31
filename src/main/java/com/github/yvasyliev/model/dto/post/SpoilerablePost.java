package com.github.yvasyliev.model.dto.post;

public abstract class SpoilerablePost extends Post {
    private boolean hasSpoiler;

    public boolean isHasSpoiler() {
        return hasSpoiler;
    }

    public void setHasSpoiler(boolean hasSpoiler) {
        this.hasSpoiler = hasSpoiler;
    }

    @Override
    public String toString() {
        return "SpoilerablePost{" +
                "hasSpoiler=" + hasSpoiler +
                "} " + super.toString();
    }
}
