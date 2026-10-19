package io.github.yvasyliev.forwarder.telegram.reddit.converter;

import io.github.yvasyliev.forwarder.telegram.core.dto.SendAnimationDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.SendPhotoDTO;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import io.github.yvasyliev.forwarder.telegram.reddit.mapper.RedditSendAnimationDTOMapper;
import io.github.yvasyliev.forwarder.telegram.reddit.mapper.RedditSendPhotoDTOMapper;
import lombok.Cleanup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedditImageConverterTest {
    @InjectMocks private RedditImageConverter imageConverter;
    @Mock private RedditSendAnimationDTOMapper sendAnimationDTOMapper;
    @Mock private RedditSendPhotoDTOMapper sendPhotoDTOMapper;

    private static Link imagePost(boolean hasGifVariant) {
        var variants = Link.Variants.builder();

        if (hasGifVariant) {
            variants.gif(Link.Variant.builder().build());
        }

        var metadata = Link.Metadata.builder()
                .variants(variants.build())
                .build();
        var preview = Link.Preview.builder()
                .images(List.of(metadata))
                .build();

        return Link.builder()
                .postHint(Link.PostHint.IMAGE)
                .preview(preview)
                .build();
    }

    @Test
    void shouldReturnEmpty() {
        var post = Link.builder()
                .postHint(Link.PostHint.LINK)
                .build();

        var actual = imageConverter.convertAll(post);

        assertThat(actual).isEmpty();
        verifyNoInteractions(sendAnimationDTOMapper, sendPhotoDTOMapper);
    }

    @Test
    void testConvertAnimation() throws IOException {
        var post = imagePost(true);
        @Cleanup var expected = mock(SendAnimationDTO.class);

        when(sendAnimationDTOMapper.map(post)).thenReturn(expected);

        var actual = imageConverter.convertAll(post);

        assertThat(actual).hasValueSatisfying(suppliers -> assertThat(suppliers)
                .singleElement()
                .satisfies(supplier -> {
                    @Cleanup var sendAnimationDTO = assertDoesNotThrow(supplier::get);

                    assertThat(sendAnimationDTO).isEqualTo(expected);
                }));

        verifyNoInteractions(sendPhotoDTOMapper);
    }

    @Test
    void testConvertPhoto() throws IOException {
        var post = imagePost(false);
        @Cleanup var expected = mock(SendPhotoDTO.class);

        when(sendPhotoDTOMapper.map(post)).thenReturn(expected);

        var actual = imageConverter.convertAll(post);

        assertThat(actual).hasValueSatisfying(suppliers -> assertThat(suppliers)
                .singleElement()
                .satisfies(supplier -> {
                    @Cleanup var sendPhotoDTO = assertDoesNotThrow(supplier::get);

                    assertThat(sendPhotoDTO).isEqualTo(expected);
                }));

        verifyNoInteractions(sendAnimationDTOMapper);
    }
}
