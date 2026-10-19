package io.github.yvasyliev.forwarder.telegram.reddit.mapper;

import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.context.Context;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedditContextMapperTest {
    private static final String TEXT = "Sample Title";
    @InjectMocks private RedditContextMapperImpl contextMapper;

    @Test
    void shouldReturnNull() {
        var actual = contextMapper.map(null);

        assertNull(actual);
    }

    @Test
    void testMap() {
        var post = mock(Link.class);
        var url = mock(URL.class);
        var expected = new Context();

        expected.setVariable("text", TEXT);
        expected.setVariable("url", url);

        when(post.title()).thenReturn(TEXT);
        when(post.url()).thenReturn(url);

        var actual = contextMapper.map(post);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
