package com.github.yvasyliev.model.dto.post;

import java.util.LinkedList;
import java.util.Queue;

public class PhotoGroupPost extends SpoilerablePost {
    private LinkedList<Queue<String>> photoUrlsPages; // TODO: 10/29/2023 Migrate to JDK 21 and replace with List<List<String>>

    public LinkedList<Queue<String>> getPhotoUrlsPages() {
        return photoUrlsPages;
    }

    public void setPhotoUrlsPages(LinkedList<Queue<String>> photoUrlsPages) {
        this.photoUrlsPages = photoUrlsPages;
    }

    @Override
    public String getType() {
        return Type.PHOTO_GROUP;
    }
}
