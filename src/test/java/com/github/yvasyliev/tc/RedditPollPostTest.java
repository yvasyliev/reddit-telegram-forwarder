package com.github.yvasyliev.tc;

import com.github.yvasyliev.model.dto.post.PollPost;
import com.github.yvasyliev.model.dto.post.Post;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedditPollPostTest extends AbstractRedditPostTest {
    @Test
    void postApproved() {
        assertDoesNotThrow(() -> stateManager.removeBlockedAuthor(pollPost().getAuthor()));

        var pollPost = pollPost();
        assertTrue(pollPost.isApproved());
        assertDoesNotThrow(() -> postManager.publishPost(pollPost, pollPost.getType()));
    }

    @Test
    void postUnapproved() {
        assertDoesNotThrow(() -> stateManager.addBlockedAuthor(pollPost().getAuthor()));

        var unapprovedPollPost = pollPost();
        assertFalse(unapprovedPollPost.isApproved());
        assertDoesNotThrow(() -> postManager.publishPost(unapprovedPollPost, unapprovedPollPost.getType()));

        var post = postManager.rejectPostCandidate(unapprovedPollPost.getCreated());
        var pollPost = assertInstanceOf(PollPost.class, post);
        pollPost.setApproved(true);
        assertDoesNotThrow(() -> postManager.publishPost(pollPost, pollPost.getType()));
    }

    Post pollPost() {
        var post = assertDoesNotThrow(() -> parsePost("json/PollPost.json"));
        assertEquals(Post.Type.POLL, post.getType());
        assertEquals(1675989471, post.getCreated());
        assertEquals("A_Canadian23", post.getAuthor());
        assertNotNull(post.getText());
        assertFalse(post.getText().isBlank());
        var pollPost = assertInstanceOf(PollPost.class, post);
        var options = pollPost.getOptions();
        assertNotNull(options);
        assertFalse(options.isEmpty());
        return pollPost;
    }
}
