package io.github.yvasyliev.forwarder.telegram.reddit.converter;

import io.github.yvasyliev.forwarder.telegram.core.dto.SendAnimationDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.SendMessageDTO;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import io.github.yvasyliev.forwarder.telegram.reddit.mapper.RedditSendAnimationDTOMapper;
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
class RedditRichVideoConverterTest {
    @InjectMocks private RedditRichVideoConverter richVideoConverter;
    @Mock private RedditSendAnimationDTOMapper sendAnimationDTOMapper;
    @Mock private RedditSendMessageDTOMapper sendMessageDTOMapper;

    private static Link.RedditVideo redditVideo(boolean isGif) {
        return Link.RedditVideo.builder()
                .isGif(isGif)
                .build();
    }

    private static Link.Preview preview(Link.RedditVideo redditVideo) {
        return Link.Preview.builder()
                .redditVideoPreview(redditVideo)
                .build();
    }

    private static Link richVideoPost(boolean isRedditMediaDomain, Link.Preview preview) {
        return Link.builder()
                .postHint(Link.PostHint.RICH_VIDEO)
                .isRedditMediaDomain(isRedditMediaDomain)
                .preview(preview)
                .build();
    }

    @Test
    void shouldReturnEmpty() {
        var post = Link.builder()
                .postHint(Link.PostHint.IMAGE)
                .build();

        var actual = richVideoConverter.convertAll(post);

        assertThat(actual).isEmpty();
        verifyNoInteractions(sendAnimationDTOMapper, sendMessageDTOMapper);
    }

    @Test
    void testConvertAnimation() throws IOException {
        var post = richVideoPost(true, preview(redditVideo(true)));
        @Cleanup var expected = mock(SendAnimationDTO.class);

        when(sendAnimationDTOMapper.map(post)).thenReturn(expected);

        var actual = richVideoConverter.convertAll(post);

        assertThat(actual).hasValueSatisfying(suppliers -> assertThat(suppliers)
                .singleElement()
                .satisfies(supplier -> {
                    @Cleanup var sendAnimationDTO = assertDoesNotThrow(supplier::get);

                    assertThat(sendAnimationDTO).isEqualTo(expected);
                }));
    }

    @Test
    void testConvertMessageForNonRedditMediaDomain() throws IOException {
        var post = richVideoPost(false, preview(redditVideo(true)));
        @Cleanup var expected = mock(SendMessageDTO.class);

        when(sendMessageDTOMapper.map(post)).thenReturn(expected);

        var actual = richVideoConverter.convertAll(post);

        assertThat(actual).hasValueSatisfying(suppliers -> assertThat(suppliers)
                .singleElement()
                .satisfies(supplier -> {
                    @Cleanup var sendMessageDTO = assertDoesNotThrow(supplier::get);

                    assertThat(sendMessageDTO).isEqualTo(expected);
                }));

        verifyNoInteractions(sendAnimationDTOMapper);
    }

    @Test
    void testConvertMessageForNullRedditVideoPreview() throws IOException {
        var post = richVideoPost(true, preview(null));
        @Cleanup var expected = mock(SendMessageDTO.class);

        when(sendMessageDTOMapper.map(post)).thenReturn(expected);

        var actual = richVideoConverter.convertAll(post);

        assertThat(actual).hasValueSatisfying(suppliers -> assertThat(suppliers)
                .singleElement()
                .satisfies(supplier -> {
                    @Cleanup var sendMessageDTO = assertDoesNotThrow(supplier::get);

                    assertThat(sendMessageDTO).isEqualTo(expected);
                }));

        verifyNoInteractions(sendAnimationDTOMapper);
    }

    @Test
    void testConvertMessageForNonGifVideoPreview() throws IOException {
        var post = richVideoPost(true, preview(redditVideo(false)));
        @Cleanup var expected = mock(SendMessageDTO.class);

        when(sendMessageDTOMapper.map(post)).thenReturn(expected);

        var actual = richVideoConverter.convertAll(post);

        assertThat(actual).hasValueSatisfying(suppliers -> assertThat(suppliers)
                .singleElement()
                .satisfies(supplier -> {
                    @Cleanup var sendMessageDTO = assertDoesNotThrow(supplier::get);

                    assertThat(sendMessageDTO).isEqualTo(expected);
                }));

        verifyNoInteractions(sendAnimationDTOMapper);
    }
}
