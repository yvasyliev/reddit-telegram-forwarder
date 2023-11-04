package com.github.yvasyliev.tc;

import com.github.yvasyliev.model.dto.post.PhotoGroupPost;
import com.github.yvasyliev.model.dto.post.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedditPhotoGroupPostTest extends AbstractRedditPostTest {
    @BeforeEach
    void waitABit() throws InterruptedException {
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    void postApproved() {
        assertDoesNotThrow(() -> stateManager.removeBlockedAuthor(photoGroupPost().getAuthor()));
        Post photoGroupPost = photoGroupPost();
        assertTrue(photoGroupPost.isApproved());
        assertDoesNotThrow(() -> postManager.publishPost(photoGroupPost, photoGroupPost.getType()));
    }

    @Test
    void postUnapproved() {
        assertDoesNotThrow(() -> stateManager.addBlockedAuthor(photoGroupPost().getAuthor()));

        Post unapprovedPhotoGroupPost = photoGroupPost();
        assertFalse(unapprovedPhotoGroupPost.isApproved());
        assertDoesNotThrow(() -> postManager.publishPost(unapprovedPhotoGroupPost, unapprovedPhotoGroupPost.getType()));

        var post = postManager.rejectPostCandidate(unapprovedPhotoGroupPost.getCreated());
        var photoGroupPost = assertInstanceOf(PhotoGroupPost.class, post);
        photoGroupPost.setApproved(true);
        assertDoesNotThrow(() -> postManager.publishPost(photoGroupPost, photoGroupPost.getType()));
    }

    Post photoGroupPost() {
        var post = assertDoesNotThrow(() -> parsePost("json/PhotoGroupPost.json"));
        assertEquals(Post.Type.PHOTO_GROUP, post.getType());
        assertEquals(1698473734, post.getCreated());
        assertEquals("nevara19", post.getAuthor());
        assertEquals("https://www.reddit.com/gallery/17i7j5d", post.getPostUrl());
        assertNotNull(post.getText());
        assertFalse(post.getText().isBlank());
        var photoGroupPost = assertInstanceOf(PhotoGroupPost.class, post);
        assertFalse(photoGroupPost.isHasSpoiler());
        var pages = photoGroupPost.getPhotoUrlsPages();
        assertNotNull(pages);
        return photoGroupPost;
    }
}
