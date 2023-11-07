package com.github.yvasyliev.tc;

import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.model.dto.post.VideoPost;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedditVideoPostTest extends AbstractRedditPostTest {
    @Test
    void postApproved() {
        assertDoesNotThrow(() -> blockedAuthorService.removeBlockedAuthor(videoPost().getAuthor()));

        var videoPost = videoPost();
        assertTrue(videoPost.isApproved());
        assertDoesNotThrow(() -> postManager.publishPost(videoPost, videoPost.getType()));
    }

    @Test
    void postUnapproved() {
        assertDoesNotThrow(() -> blockedAuthorService.saveBlockedAuthor(videoPost().getAuthor()));

        var unapprovedVideoPost = videoPost();
        assertFalse(unapprovedVideoPost.isApproved());
        assertDoesNotThrow(() -> postManager.publishPost(unapprovedVideoPost, unapprovedVideoPost.getType()));

        var post = postManager.rejectPostCandidate(unapprovedVideoPost.getCreated());
        var videoPost = assertInstanceOf(VideoPost.class, post);
        videoPost.setApproved(true);
        assertDoesNotThrow(() -> postManager.publishPost(videoPost, videoPost.getType()));
    }

    Post videoPost() {
        var post = assertDoesNotThrow(() -> parsePost("json/VideoPost.json"));
        assertEquals(Post.Type.VIDEO, post.getType());
        assertEquals(1698463733, post.getCreated());
        assertEquals("Nightmare_Fire", post.getAuthor());
        assertEquals("https://v.redd.it/3akewp074vwb1", post.getPostUrl());
        assertNotNull(post.getText());
        assertFalse(post.getText().isBlank());
        var videoPost = assertInstanceOf(VideoPost.class, post);
        assertFalse(videoPost.isHasSpoiler());
        assertEquals("https://sd.rapidsave.com/download.php?permalink=https://reddit.com/r/cats/comments/17i510y/bite_disguised_as_a_yawn/&video_url=https://v.redd.it/3akewp074vwb1/DASH_270.mp4?source=fallback&audio_url=false", videoPost.getMediaUrl());
        return videoPost;
    }
}
