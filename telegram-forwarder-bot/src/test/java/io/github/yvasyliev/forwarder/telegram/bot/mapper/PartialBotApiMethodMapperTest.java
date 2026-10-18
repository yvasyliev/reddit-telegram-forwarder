package io.github.yvasyliev.forwarder.telegram.bot.mapper;

import io.github.yvasyliev.forwarder.telegram.core.configuration.TelegramAdminProperties;
import io.github.yvasyliev.forwarder.telegram.core.dto.InputFileDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.InputMediaDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.PartialBotApiMethodDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.SendAnimationDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.SendMediaGroupDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.SendMessageDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.SendPhotoDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.SendVideoDTO;
import io.github.yvasyliev.forwarder.telegram.core.util.CloseableArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartialBotApiMethodMapperTest {
    @InjectMocks private PartialBotApiMethodMapperImpl partialBotApiMethodMapper;
    @Mock private InputFileMapper inputFileMapper;
    @Mock private InputMediaMapper inputMediaMapper;

    @Test
    void testMapNull() {
        var actual = partialBotApiMethodMapper.map((PartialBotApiMethodDTO) null, null);

        assertNull(actual);
    }

    @Test
    void shouldMapSendAnimationDTO() {
        var animationDTO = mock(InputFileDTO.class);
        var caption = "caption";
        var hasSpoiler = true;
        var chatId = 12345L;
        var sendAnimationDTO = new SendAnimationDTO(animationDTO, caption, hasSpoiler);
        var adminProperties = new TelegramAdminProperties(chatId);
        var animation = mock(InputFile.class);
        var expected = SendAnimation.builder()
                .chatId(chatId)
                .animation(animation)
                .caption(caption)
                .hasSpoiler(hasSpoiler)
                .build();

        when(inputFileMapper.map(animationDTO)).thenReturn(animation);

        var actual = partialBotApiMethodMapper.map(sendAnimationDTO, adminProperties);

        assertEquals(expected, actual);
    }

    @Test
    void shouldMapSendMediaGroupDTO() {
        var inputMediaDTO = mock(InputMediaDTO.class);
        var caption = "caption";
        var chatId = 12345L;
        var medias = new CloseableArrayList<InputMediaDTO>();
        var inputMedia = mock(InputMedia.class);
        var sendMediaGroupDTO = new SendMediaGroupDTO(medias, caption);
        var adminProperties = new TelegramAdminProperties(chatId);
        var expected = SendMediaGroup.builder().chatId(chatId).media(inputMedia).build();

        medias.add(inputMediaDTO);

        when(inputMediaMapper.map(inputMediaDTO)).thenReturn(inputMedia);

        var actual = partialBotApiMethodMapper.map(sendMediaGroupDTO, adminProperties);

        assertEquals(expected, actual);
        verify(inputMedia).setCaption(caption);
    }

    @Test
    void shouldMapEmptySendMediaGroupDTO() {
        var caption = "caption";
        var chatId = 12345L;
        var medias = new CloseableArrayList<InputMediaDTO>();
        var sendMediaGroupDTO = new SendMediaGroupDTO(medias, caption);
        var adminProperties = new TelegramAdminProperties(chatId);
        var expected = SendMediaGroup.builder().chatId(chatId).build();

        var actual = partialBotApiMethodMapper.map(sendMediaGroupDTO, adminProperties);

        assertEquals(expected, actual);
    }

    @Test
    void shouldMapSendMessageDTO() {
        var text = "text";
        var chatId = 12345L;
        var methodDTO = (PartialBotApiMethodDTO) new SendMessageDTO(text);
        var adminProperties = new TelegramAdminProperties(chatId);
        var expected = SendMessage.builder().chatId(chatId).text(text).parseMode(ParseMode.HTML).build();

        var actual = partialBotApiMethodMapper.map(methodDTO, adminProperties);

        assertEquals(expected, actual);
    }

    @Test
    void testMapSendMessageNull() {
        var actual = partialBotApiMethodMapper.map((SendMessageDTO) null, null);

        assertNull(actual);
    }

    @Test
    void shouldMapSendPhotoDTO() {
        var photoDTO = mock(InputFileDTO.class);
        var caption = "caption";
        var hasSpoiler = true;
        var chatId = 12345L;
        var sendPhotoDTO = new SendPhotoDTO(photoDTO, caption, hasSpoiler);
        var adminProperties = new TelegramAdminProperties(chatId);
        var photo = mock(InputFile.class);
        var expected = SendPhoto.builder().chatId(chatId).photo(photo).caption(caption).hasSpoiler(hasSpoiler).build();

        when(inputFileMapper.map(photoDTO)).thenReturn(photo);

        var actual = partialBotApiMethodMapper.map(sendPhotoDTO, adminProperties);

        assertEquals(expected, actual);
    }

    @Test
    void shouldMapSendVideoDTO() {
        var videoDTO = mock(InputFileDTO.class);
        var caption = "caption";
        var hasSpoiler = true;
        var chatId = 12345L;
        var video = mock(InputFile.class);
        var sendVideoDTO = new SendVideoDTO(videoDTO, caption, hasSpoiler);
        var adminProperties = new TelegramAdminProperties(chatId);
        var expected = SendVideo.builder()
                .chatId(chatId)
                .video(video)
                .caption(caption)
                .hasSpoiler(hasSpoiler)
                .build();

        when(inputFileMapper.map(videoDTO)).thenReturn(video);

        var actual = partialBotApiMethodMapper.map(sendVideoDTO, adminProperties);

        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowExceptionForUnknownSubclass() {
        var methodDTO = mock(PartialBotApiMethodDTO.class);
        var adminProperties = new TelegramAdminProperties(12345L);

        assertThrows(
                IllegalArgumentException.class,
                () -> partialBotApiMethodMapper.map(methodDTO, adminProperties)
        );
    }
}
