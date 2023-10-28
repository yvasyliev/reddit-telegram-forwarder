package com.github.yvasyliev.tc;

import com.github.yvasyliev.model.dto.post.PhotoPost;
import com.github.yvasyliev.model.dto.post.Post;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedditPhotoPostTest extends AbstractRedditPostTest {
    @Test
    void postApproved() {
        assertDoesNotThrow(() -> stateManager.removeBlockedAuthor(photoPost().getAuthor()));

        var photoPost = photoPost();
        assertTrue(photoPost.isApproved());
        assertDoesNotThrow(() -> postManager.publishPost(photoPost, photoPost.getType()));
    }

    @Test
    void postUnapproved() {
        assertDoesNotThrow(() -> stateManager.addBlockedAuthor(photoPost().getAuthor()));

        var unapprovedPhotoPost = photoPost();
        assertFalse(unapprovedPhotoPost.isApproved());
        assertDoesNotThrow(() -> postManager.publishPost(unapprovedPhotoPost, unapprovedPhotoPost.getType()));

        var post = postManager.rejectPostCandidate(unapprovedPhotoPost.getCreated());
        var photoPost = assertInstanceOf(PhotoPost.class, post);
        photoPost.setApproved(true);
        assertDoesNotThrow(() -> postManager.publishPost(photoPost, photoPost.getType()));
    }

    Post photoPost() {
        var post = assertDoesNotThrow(() -> parsePost("json/PhotoPost.json"));
        assertEquals(Post.Type.PHOTO, post.getType());
        assertEquals(1698428408, post.getCreated());
        assertEquals("SourTruffles", post.getAuthor());
        assertEquals("https://i.redd.it/2rtjf4hn7swb1.jpg", post.getPostUrl());
        assertNotNull(post.getText());
        assertFalse(post.getText().isBlank());
        var photoPost = assertInstanceOf(PhotoPost.class, post);
        assertFalse(photoPost.isHasSpoiler());
        assertEquals("https://i.redd.it/2rtjf4hn7swb1.jpg", photoPost.getMediaUrl());
        return photoPost;
    }
}
