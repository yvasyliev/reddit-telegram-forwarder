package io.github.yvasyliev.forwarder.telegram.reddit.converter;

import io.github.yvasyliev.forwarder.telegram.core.dto.SendAnimationDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.SendMediaGroupDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.SendPhotoDTO;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import io.github.yvasyliev.forwarder.telegram.reddit.mapper.RedditSendAnimationDTOMapper;
import io.github.yvasyliev.forwarder.telegram.reddit.mapper.RedditSendMediaGroupDTOMapper;
import io.github.yvasyliev.forwarder.telegram.reddit.mapper.RedditSendPhotoDTOMapper;
import io.github.yvasyliev.forwarder.telegram.reddit.util.GalleryMetadataPartitioner;
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
class RedditGalleryDataConverterTest {
    @InjectMocks private RedditGalleryDataConverter galleryDataConverter;
    @Mock private GalleryMetadataPartitioner galleryMetadataPartitioner;
    @Mock private RedditSendAnimationDTOMapper sendAnimationDTOMapper;
    @Mock private RedditSendPhotoDTOMapper sendPhotoDTOMapper;
    @Mock private RedditSendMediaGroupDTOMapper sendMediaGroupDTOMapper;

    private static Link.Metadata metadata(Link.Metadata.Type type) {
        return Link.Metadata.builder()
                .type(type)
                .build();
    }

    private static Link galleryPost(boolean isNsfw) {
        return Link.builder()
                .title("caption")
                .thumbnail(isNsfw ? "nsfw" : null)
                .galleryData(new Link.GalleryData(List.of()))
                .build();
    }

    @Test
    void shouldReturnEmpty() {
        var post = Link.builder().build();

        var actual = galleryDataConverter.convertAll(post);

        assertThat(actual).isEmpty();
        verifyNoInteractions(
                galleryMetadataPartitioner,
                sendAnimationDTOMapper,
                sendPhotoDTOMapper,
                sendMediaGroupDTOMapper
        );
    }

    @Test
    void testConvertMediaGroups() throws IOException {
        var firstPartition = List.of(metadata(Link.Metadata.Type.IMAGE), metadata(Link.Metadata.Type.IMAGE));
        var secondPartition = List.of(metadata(Link.Metadata.Type.IMAGE), metadata(Link.Metadata.Type.IMAGE));
        var post = galleryPost(false);
        @Cleanup var expectedFirstMediaGroup = mock(SendMediaGroupDTO.class);
        @Cleanup var expectedSecondMediaGroup = mock(SendMediaGroupDTO.class);

        when(galleryMetadataPartitioner.partition(post)).thenReturn(List.of(firstPartition, secondPartition));
        when(sendMediaGroupDTOMapper.map(firstPartition, false, "caption")).thenReturn(expectedFirstMediaGroup);
        when(sendMediaGroupDTOMapper.map(secondPartition, false, null)).thenReturn(expectedSecondMediaGroup);

        var actual = galleryDataConverter.convertAll(post);

        assertThat(actual).hasValueSatisfying(suppliers -> assertThat(suppliers)
                .satisfiesExactly(
                        supplier -> {
                            @Cleanup var sendMediaGroupDTO = assertDoesNotThrow(supplier::get);

                            assertThat(sendMediaGroupDTO).isEqualTo(expectedFirstMediaGroup);
                        },
                        supplier -> {
                            @Cleanup var sendMediaGroupDTO = assertDoesNotThrow(supplier::get);

                            assertThat(sendMediaGroupDTO).isEqualTo(expectedSecondMediaGroup);
                        }));
    }

    @Test
    void testConvertSingleMedia() throws IOException {
        var animatedImageMetadata = metadata(Link.Metadata.Type.ANIMATED_IMAGE);
        var imageMetadata = metadata(Link.Metadata.Type.IMAGE);
        var post = galleryPost(true);
        @Cleanup var expectedAnimation = mock(SendAnimationDTO.class);
        @Cleanup var expectedPhoto = mock(SendPhotoDTO.class);

        when(galleryMetadataPartitioner.partition(post)).thenReturn(List.of(
                List.of(animatedImageMetadata),
                List.of(imageMetadata)
        ));
        when(sendAnimationDTOMapper.map(animatedImageMetadata, true)).thenReturn(expectedAnimation);
        when(sendPhotoDTOMapper.map(imageMetadata, true)).thenReturn(expectedPhoto);

        var actual = galleryDataConverter.convertAll(post);

        assertThat(actual).hasValueSatisfying(suppliers -> assertThat(suppliers)
                .satisfiesExactly(
                        supplier -> {
                            @Cleanup var sendAnimationDTO = assertDoesNotThrow(supplier::get);

                            assertThat(sendAnimationDTO).isEqualTo(expectedAnimation);
                        },
                        supplier -> {
                            @Cleanup var sendPhotoDTO = assertDoesNotThrow(supplier::get);

                            assertThat(sendPhotoDTO).isEqualTo(expectedPhoto);
                        }));
    }
}
