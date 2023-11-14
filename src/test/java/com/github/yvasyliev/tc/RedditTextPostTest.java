package com.github.yvasyliev.tc;

import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.model.dto.post.TextPost;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedditTextPostTest extends AbstractRedditPostTest {
    @Test
    void postApproved() {
        assertDoesNotThrow(() -> blockedAuthorService.removeBlockedAuthor(textPost().getAuthor()));
        Post textPost = textPost();
        assertTrue(textPost.isApproved());
        assertDoesNotThrow(() -> postManager.publishPost(textPost, textPost.getType()));
    }

    @Test
    void postUnapproved() {
        assertDoesNotThrow(() -> blockedAuthorService.saveBlockedAuthor(textPost().getAuthor()));

        Post unapprovedTextPost = textPost();
        assertFalse(unapprovedTextPost.isApproved());
        assertDoesNotThrow(() -> postManager.publishPost(unapprovedTextPost, unapprovedTextPost.getType()));

        var post = postManager.rejectPostCandidate(unapprovedTextPost.getCreated());
        var textPost = assertInstanceOf(TextPost.class, post);
        textPost.setApproved(true);
        assertDoesNotThrow(() -> postManager.publishPost(textPost, textPost.getType()));
    }

    Post textPost() {
        var post = assertDoesNotThrow(() -> parsePost("json/TextPost.json"));
        assertEquals(Post.Type.TEXT, post.getType());
        assertEquals(1698365958, post.getCreated());
        assertEquals("thr1ceuponatime", post.getAuthor());
        assertEquals("https://www.indiewire.com/news/general-news/martin-scorsese-joins-letterboxd-lists-double-feature-choices-1234920446/", post.getPostUrl());
        assertNotNull(post.getText());
        assertFalse(post.getText().isBlank());
        return assertInstanceOf(TextPost.class, post);
    }
}