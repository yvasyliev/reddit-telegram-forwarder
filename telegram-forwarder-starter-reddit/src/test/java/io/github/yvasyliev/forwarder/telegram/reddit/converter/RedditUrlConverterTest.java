package io.github.yvasyliev.forwarder.telegram.reddit.converter;

import io.github.yvasyliev.forwarder.telegram.core.dto.SendMessageDTO;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import io.github.yvasyliev.forwarder.telegram.reddit.mapper.RedditSendMessageDTOMapper;
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
class RedditUrlConverterTest {
    @InjectMocks private RedditUrlConverter urlConverter;
    @Mock private RedditSendMessageDTOMapper sendMessageDTOMapper;

    @Test
    void shouldReturnEmpty() {
        var post = mock(Link.class);

        when(post.postHint()).thenReturn(Link.PostHint.IMAGE);

        var actual = urlConverter.convertAll(post);

        assertThat(actual).isEmpty();
        verifyNoInteractions(sendMessageDTOMapper);
    }

    @Test
    void testDoConvert() throws IOException {
        var post = mock(Link.class);
        @Cleanup var expected = mock(SendMessageDTO.class);

        when(post.postHint()).thenReturn(Link.PostHint.LINK);
        when(sendMessageDTOMapper.map(post)).thenReturn(expected);

        var actual = urlConverter.convertAll(post);

        assertThat(actual).hasValueSatisfying(suppliers -> assertThat(suppliers)
                .singleElement()
                .satisfies(supplier -> {
                    @Cleanup var sendMessageDTO = assertDoesNotThrow(supplier::get);

                    assertThat(sendMessageDTO).isEqualTo(expected);
                }));
    }
}
