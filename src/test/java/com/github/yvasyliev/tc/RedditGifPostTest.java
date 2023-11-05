package com.github.yvasyliev.tc;

import com.github.yvasyliev.model.dto.post.GifPost;
import com.github.yvasyliev.model.dto.post.Post;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedditGifPostTest extends AbstractRedditPostTest {
    @AfterAll
    static void waitABit() throws InterruptedException {
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    void postApproved() {
        assertDoesNotThrow(() -> stateManager.removeBlockedAuthor(gifPost().getAuthor()));

        var gifPost = gifPost();
        assertTrue(gifPost.isApproved());
        assertDoesNotThrow(() -> postManager.publishPost(gifPost, gifPost.getType()));
    }

    @Test
    void postUnapproved() {
        assertDoesNotThrow(() -> stateManager.addBlockedAuthor(gifPost().getAuthor()));

        var unapprovedGifPost = gifPost();
        assertFalse(unapprovedGifPost.isApproved());
        assertDoesNotThrow(() -> postManager.publishPost(unapprovedGifPost, unapprovedGifPost.getType()));

        var post = postManager.rejectPostCandidate(unapprovedGifPost.getCreated());
        var gifPost = assertInstanceOf(GifPost.class, post);
        gifPost.setApproved(true);
        assertDoesNotThrow(() -> postManager.publishPost(gifPost, gifPost.getType()));
    }

    Post gifPost() {
        var post = assertDoesNotThrow(() -> parsePost("json/GifPost.json"));
        assertEquals(Post.Type.GIF, post.getType());
        assertEquals(1665430031, post.getCreated());
        assertEquals("jeh366", post.getAuthor());
        assertEquals("https://i.redd.it/f3wlnnpv41t91.gif", post.getPostUrl());
        assertNotNull(post.getText());
        assertFalse(post.getText().isBlank());
        var photoPost = assertInstanceOf(GifPost.class, post);
        assertFalse(photoPost.isHasSpoiler());
        assertEquals("https://preview.redd.it/f3wlnnpv41t91.gif?format=mp4&s=5a024f969808538ff3ad387573cb14328e882080", photoPost.getMediaUrl());
        return photoPost;
    }
}
