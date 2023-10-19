package com.github.yvasyliev.model.dto.post;

import java.util.List;

public class PhotoGroupPost extends SpoilerablePost {
    private List<List<String>> photoUrlsPages;

    public List<List<String>> getPhotoUrlsPages() {
        return photoUrlsPages;
    }

    public void setPhotoUrlsPages(List<List<String>> photoUrlsPages) {
        this.photoUrlsPages = photoUrlsPages;
    }

    @Override
    public String getType() {
        return Type.PHOTO_GROUP;
    }
}
