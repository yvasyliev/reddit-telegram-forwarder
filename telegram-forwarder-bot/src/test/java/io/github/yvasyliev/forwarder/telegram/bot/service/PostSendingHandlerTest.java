package io.github.yvasyliev.forwarder.telegram.bot.service;

import io.github.yvasyliev.forwarder.telegram.bot.mapper.PartialBotApiMethodMapper;
import io.github.yvasyliev.forwarder.telegram.core.configuration.TelegramAdminProperties;
import io.github.yvasyliev.forwarder.telegram.core.dto.PartialBotApiMethodDTO;
import io.github.yvasyliev.forwarder.telegram.core.util.CloseableSupplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostSendingHandlerTest {
    @InjectMocks private PostSendingHandler postSendingHandler;
    @Mock private TelegramAdminProperties adminProperties;
    @Mock private PartialBotApiMethodMapper botApiMethodMapper;
    @Mock private TelegramClient telegramClient;
    @Mock private CloseableSupplier<PartialBotApiMethodDTO> payload;
    @Mock private PartialBotApiMethodDTO methodDTO;

    @Test
    void shouldSendAnimation() throws IOException, TelegramApiException {
        var sendAnimation = mock(SendAnimation.class);
        var message = mock(Message.class);

        shouldSend(sendAnimation);
        when(telegramClient.execute(sendAnimation)).thenReturn(message);

        var actual = postSendingHandler.handle(payload, null);

        assertEquals(message, actual);
        verify(methodDTO).close();
    }

    @Test
    void shouldSendMediaGroup() throws IOException, TelegramApiException {
        var sendMediaGroup = mock(SendMediaGroup.class);
        var messages = List.of(mock(Message.class));

        shouldSend(sendMediaGroup);
        when(telegramClient.execute(sendMediaGroup)).thenReturn(messages);

        var actual = postSendingHandler.handle(payload, null);

        assertEquals(messages, actual);
        verify(methodDTO).close();
    }

    @Test
    void shouldSendMessage() throws IOException, TelegramApiException {
        var sendMessage = mock(SendMessage.class);
        var message = mock(Message.class);

        shouldSend(sendMessage);
        when(telegramClient.execute(sendMessage)).thenReturn(message);

        var actual = postSendingHandler.handle(payload, null);

        assertEquals(message, actual);
        verify(methodDTO).close();
    }

    @Test
    void shouldSendPhoto() throws IOException, TelegramApiException {
        var sendPhoto = mock(SendPhoto.class);
        var message = mock(Message.class);

        shouldSend(sendPhoto);
        when(telegramClient.execute(sendPhoto)).thenReturn(message);

        var actual = postSendingHandler.handle(payload, null);

        assertEquals(message, actual);
        verify(methodDTO).close();
    }

    @Test
    void shouldSendVideo() throws IOException, TelegramApiException {
        var sendVideo = mock(SendVideo.class);
        var message = mock(Message.class);

        shouldSend(sendVideo);
        when(telegramClient.execute(sendVideo)).thenReturn(message);

        var actual = postSendingHandler.handle(payload, null);

        assertEquals(message, actual);
        verify(methodDTO).close();
    }

    @Test
    void shouldHandlePayloadIOException() throws IOException {
        when(payload.get()).thenThrow(IOException.class);

        assertNull(postSendingHandler.handle(payload, null));

        verifyNoInteractions(telegramClient);
    }

    @Test
    void shouldHandleCloseIOException() throws IOException, TelegramApiException {
        var sendMessage = mock(SendMessage.class);

        shouldSend(sendMessage);
        doThrow(IOException.class).when(methodDTO).close();

        assertNull(postSendingHandler.handle(payload, null));
        verify(telegramClient).execute(sendMessage);
    }

    @Test
    void shouldHandleTelegramApiException() throws IOException, TelegramApiException {
        var sendMessage = mock(SendMessage.class);

        shouldSend(sendMessage);
        when(telegramClient.execute(sendMessage)).thenThrow(TelegramApiException.class);

        assertNull(postSendingHandler.handle(payload, null));
        verify(methodDTO).close();
    }

    @Test
    void shouldThrowExceptionForUnsupportedMethod() throws IOException {
        var method = mock(PartialBotApiMethod.class);

        shouldSend(method);

        assertThrows(IllegalArgumentException.class, () -> postSendingHandler.handle(payload, null));
        verify(methodDTO).close();
    }

    private void shouldSend(PartialBotApiMethod<?> method) throws IOException {
        when(payload.get()).thenReturn(methodDTO);
        doReturn(method).when(botApiMethodMapper).map(methodDTO, adminProperties);
    }
}
