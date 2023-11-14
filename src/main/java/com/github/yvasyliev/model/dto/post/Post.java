package com.github.yvasyliev.model.dto.post;

public abstract class Post implements Comparable<Post> {
    private int created;
    private String author;
    private boolean approved;
    private String postUrl;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public abstract String getType();

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    @Override
    public int compareTo(Post post) {
        return Integer.compare(created, post.created);
    }

    @Override
    public String toString() {
        return "Post{" +
                "created=" + created +
                ", author='" + author + '\'' +
                ", approved=" + approved +
                ", postUrl='" + postUrl + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public interface Type {
        String GIF = "GIF";
        String PHOTO_GROUP = "PHOTO_GROUP";
        String PHOTO = "PHOTO";
        String POLL = "POLL";
        String TEXT = "TEXT";
        String VIDEO = "VIDEO";
    }
}
