package io.github.yvasyliev.forwarder.telegram.reddit.mapper;

import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import io.github.yvasyliev.forwarder.telegram.thymeleaf.TemplateContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.IContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedditTemplateContextMapperTest {
    @InjectMocks private RedditTemplateContextMapperImpl templateContextMapper;
    @Mock private RedditContextMapper redditContextMapper;

    @Test
    void shouldReturnNull() {
        var actual = templateContextMapper.map(null);

        assertNull(actual);
        verifyNoInteractions(redditContextMapper);
    }

    @Test
    void testMap() {
        var post = mock(Link.class);
        var context = mock(IContext.class);
        var expected = new TemplateContext("url", context);

        when(redditContextMapper.map(post)).thenReturn(context);

        var actual = templateContextMapper.map(post);

        assertEquals(expected, actual);
    }
}
