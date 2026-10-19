package io.github.yvasyliev.forwarder.telegram.reddit.converter;

import io.github.yvasyliev.forwarder.telegram.core.dto.PartialBotApiMethodDTO;
import io.github.yvasyliev.forwarder.telegram.core.util.CloseableSupplier;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedditLinkConverterResolverTest {
    private RedditLinkConverterResolver converterManager;
    @Mock private RedditLinkConverter converter;

    @BeforeEach
    void setUp() {
        converterManager = new RedditLinkConverterResolver(List.of(converter));
    }

    @Test
    void testConvert() {
        var post = mock(Link.class);
        var expected = List.of(Mockito.<CloseableSupplier<PartialBotApiMethodDTO>>mock());

        when(converter.convertAll(post)).thenReturn(Optional.of(expected));

        var actual = converterManager.convert(post);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfNoConverterFound() {
        var post = mock(Link.class);

        when(converter.convertAll(post)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> converterManager.convert(post));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionIfNoConverters() {
        var post = mock(Link.class);
        var resolver = new RedditLinkConverterResolver(List.of());

        assertThrows(IllegalArgumentException.class, () -> resolver.convert(post));
    }
}
