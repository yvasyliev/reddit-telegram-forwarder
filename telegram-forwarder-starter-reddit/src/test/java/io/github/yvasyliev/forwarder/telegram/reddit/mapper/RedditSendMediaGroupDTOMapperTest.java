package io.github.yvasyliev.forwarder.telegram.reddit.mapper;

import io.github.yvasyliev.forwarder.telegram.core.dto.InputMediaPhotoDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.InputMediaVideoDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.SendMediaGroupDTO;
import io.github.yvasyliev.forwarder.telegram.core.util.CloseableArrayList;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import lombok.Cleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedditSendMediaGroupDTOMapperTest {
    private static final boolean HAS_SPOILER = true;
    private static final String CAPTION = "Sample Caption";
    private final RedditSendMediaGroupDTOMapper mapper = Mappers.getMapper(RedditSendMediaGroupDTOMapper.class);
    @Mock private RedditInputMediaVideoDTOMapper inputMediaVideoDTOMapper;
    @Mock private RedditInputMediaPhotoDTOMapper inputMediaPhotoDTOMapper;

    @BeforeEach
    void setUp() {
        mapper.setInputMediaVideoDTOMapper(inputMediaVideoDTOMapper);
        mapper.setInputMediaPhotoDTOMapper(inputMediaPhotoDTOMapper);
    }

    @Test
    void shouldReturnNull() throws IOException {
        @Cleanup var actual = assertDoesNotThrow(() -> mapper.map(null, HAS_SPOILER, null));

        assertNull(actual);
        verifyNoInteractions(inputMediaVideoDTOMapper, inputMediaPhotoDTOMapper);
    }

    @Test
    void shouldReturnNullMedias() {
        var expected = new SendMediaGroupDTO(null, CAPTION);
        var actual = assertDoesNotThrow(() -> mapper.map(null, HAS_SPOILER, CAPTION));

        assertEquals(expected, actual);
        verifyNoInteractions(inputMediaVideoDTOMapper, inputMediaPhotoDTOMapper);
    }

    @Test
    void shouldReturnEmptyMedias() throws IOException {
        @Cleanup var expected = new SendMediaGroupDTO(new CloseableArrayList<>(), CAPTION);
        @Cleanup var actual = assertDoesNotThrow(() -> mapper.map(List.of(), HAS_SPOILER, CAPTION));

        assertEquals(expected, actual);
    }

    @Test
    void testMapImage() throws IOException {
        var metadata = mock(Link.Metadata.class);
        var image = mock(InputMediaPhotoDTO.class);
        @Cleanup var expected = new SendMediaGroupDTO(new CloseableArrayList<>(), CAPTION);

        expected.medias().add(image);

        when(metadata.type()).thenReturn(Link.Metadata.Type.IMAGE);
        when(inputMediaPhotoDTOMapper.map(metadata, HAS_SPOILER)).thenReturn(image);

        @Cleanup var actual = assertDoesNotThrow(() -> mapper.map(List.of(metadata), HAS_SPOILER, CAPTION));

        assertEquals(expected, actual);
    }

    @Test
    void testMapAnimatedImage() throws IOException {
        var metadata = mock(Link.Metadata.class);
        var video = mock(InputMediaVideoDTO.class);
        @Cleanup var expected = new SendMediaGroupDTO(new CloseableArrayList<>(), CAPTION);

        expected.medias().add(video);

        when(metadata.type()).thenReturn(Link.Metadata.Type.ANIMATED_IMAGE);
        when(inputMediaVideoDTOMapper.map(metadata, HAS_SPOILER)).thenReturn(video);

        var actual = assertDoesNotThrow(() -> mapper.map(List.of(metadata), HAS_SPOILER, CAPTION));

        assertEquals(expected, actual);
    }
}
