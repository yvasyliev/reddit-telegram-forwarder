package com.github.yvasyliev.model.dto;

import java.util.List;

public class Post implements Comparable<Post> {
    private PostType type;
    private Integer created = 0;
    private String author;
    private String text;
    private Boolean hasSpoiler;
    private Boolean approved;
    private String mediaUrl;
    private List<List<String>> photoUrlsPages;
    private List<String> options;
    private String postUrl;

    public PostType getType() {
        return type;
    }

    public void setType(PostType type) {
        this.type = type;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean isHasSpoiler() {
        return hasSpoiler;
    }

    public void setHasSpoiler(Boolean hasSpoiler) {
        this.hasSpoiler = hasSpoiler;
    }

    public Boolean isApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public List<List<String>> getPhotoUrlsPages() {
        return photoUrlsPages;
    }

    public void setPhotoUrlsPages(List<List<String>> photoUrlsPages) {
        this.photoUrlsPages = photoUrlsPages;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    @Override
    public int compareTo(Post post) {
        return Integer.compare(created, post.getCreated());
    }

    @Override
    public String toString() {
        return "Post{" +
                "type=" + type +
                ", created=" + created +
                ", author='" + author + '\'' +
                ", text='" + text + '\'' +
                ", hasSpoiler=" + hasSpoiler +
                ", approved=" + approved +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", photoUrlsPages=" + photoUrlsPages +
                ", options=" + options +
                ", postUrl='" + postUrl + '\'' +
                '}';
    }
}
