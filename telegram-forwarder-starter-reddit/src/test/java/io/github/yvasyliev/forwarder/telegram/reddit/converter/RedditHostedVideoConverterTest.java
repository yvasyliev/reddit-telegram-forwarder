package io.github.yvasyliev.forwarder.telegram.reddit.converter;

import io.github.yvasyliev.forwarder.telegram.core.dto.SendVideoDTO;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import io.github.yvasyliev.forwarder.telegram.reddit.mapper.RedditSendVideoDTOMapper;
import lombok.Cleanup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedditHostedVideoConverterTest {
    @InjectMocks private RedditHostedVideoConverter hostedVideoConverter;
    @Mock private RedditSendVideoDTOMapper sendVideoDTOMapper;

    @Test
    void shouldReturnEmpty() {
        var post = mock(Link.class);

        when(post.postHint()).thenReturn(Link.PostHint.IMAGE);

        var actual = hostedVideoConverter.convertAll(post);

        assertThat(actual).isEmpty();
        verifyNoInteractions(sendVideoDTOMapper);
    }

    @Test
    void testDoConvert() throws IOException {
        var post = mock(Link.class);
        @Cleanup var expected = mock(SendVideoDTO.class);

        when(post.postHint()).thenReturn(Link.PostHint.HOSTED_VIDEO);
        when(sendVideoDTOMapper.map(post)).thenReturn(expected);

        var actual = hostedVideoConverter.convertAll(post);

        assertThat(actual).hasValueSatisfying(suppliers -> assertThat(suppliers)
                .singleElement()
                .satisfies(supplier -> {
                    @Cleanup var sendVideoDTO = assertDoesNotThrow(supplier::get);

                    assertThat(sendVideoDTO).isEqualTo(expected);
                }));
    }
}
