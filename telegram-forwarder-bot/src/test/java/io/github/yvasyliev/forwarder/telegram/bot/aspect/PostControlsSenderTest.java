package io.github.yvasyliev.forwarder.telegram.bot.aspect;

import io.github.yvasyliev.forwarder.telegram.bot.configuration.PostControlsSendMessageProperties;
import io.github.yvasyliev.forwarder.telegram.bot.mapper.PartialBotApiMethodMapper;
import io.github.yvasyliev.forwarder.telegram.bot.mapper.SendMessageMapper;
import io.github.yvasyliev.forwarder.telegram.bot.service.PostSendingHandler;
import io.github.yvasyliev.forwarder.telegram.core.configuration.TelegramAdminProperties;
import io.github.yvasyliev.forwarder.telegram.core.dto.PartialBotApiMethodDTO;
import io.github.yvasyliev.forwarder.telegram.core.service.PostSender;
import io.github.yvasyliev.forwarder.telegram.core.util.CloseableSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostControlsSenderTest {
    @InjectMocks private PostControlsSender postControlsSender;
    @Mock private TelegramClient telegramClient;
    @Mock private SendMessageMapper sendMessageMapper;
    @Mock private PostControlsSendMessageProperties postControlsSendMessageProperties;
    @Mock private Message message;
    @Mock private SendMessage sendMessage;
    @Mock private TelegramAdminProperties adminProperties;

    private void stubPostControlsSendMessage() {
        when(sendMessageMapper.map(postControlsSendMessageProperties, List.of(message))).thenReturn(sendMessage);
    }

    @Nested
    class AdvicePointcutTest {
        @Mock private PartialBotApiMethodMapper botApiMethodMapper;
        @Mock private PartialBotApiMethodDTO methodDTO;
        @Mock private CloseableSupplier<PartialBotApiMethodDTO> payload;

        @BeforeEach
        void setUp() {
            stubPostControlsSendMessage();
        }

        @Test
        void shouldSendPostControlKeyboardWhenPostSendingHandlerSendsPost() throws Exception {
            var postMessage = mock(SendMessage.class);

            when(payload.get()).thenReturn(methodDTO);
            doReturn(postMessage).when(botApiMethodMapper).map(methodDTO, adminProperties);
            when(telegramClient.execute(postMessage)).thenReturn(message);

            createPostSendingHandlerProxy().handle(payload, null);

            verify(telegramClient).execute(sendMessage);
        }

        @Test
        void shouldSendPostControlKeyboardWhenPostSendingHandlerSendsMediaGroup() throws Exception {
            var sendMediaGroup = mock(SendMediaGroup.class);

            when(payload.get()).thenReturn(methodDTO);
            doReturn(sendMediaGroup).when(botApiMethodMapper).map(methodDTO, adminProperties);
            when(telegramClient.execute(sendMediaGroup)).thenReturn(List.of(message));

            createPostSendingHandlerProxy().handle(payload, null);

            verify(telegramClient).execute(sendMessage);
        }

        @Test
        void shouldSendPostControlKeyboardWhenPostSenderSendsPost() throws Exception {
            var postSender = new MessagePostSender(message);

            createProxy(postSender).send(sendMessage);

            verify(telegramClient).execute(sendMessage);
        }

        @Test
        void shouldSendPostControlKeyboardWhenPostSenderSendsMediaGroup() throws Exception {
            var postSender = new MediaGroupPostSender(List.of(message));

            createProxy(postSender).send(mock(SendMediaGroup.class));

            verify(telegramClient).execute(sendMessage);
        }

        private PostSendingHandler createPostSendingHandlerProxy() {
            return createProxy(new PostSendingHandler(adminProperties, botApiMethodMapper, telegramClient));
        }

        private <T> T createProxy(T target) {
            var proxyFactory = new AspectJProxyFactory(target);

            proxyFactory.setProxyTargetClass(true);
            proxyFactory.addAspect(postControlsSender);

            return proxyFactory.getProxy();
        }

        static class MessagePostSender implements PostSender<SendMessage, Message> {
            private final Message message;

            MessagePostSender(Message message) {
                this.message = message;
            }

            @Override
            public Message send(SendMessage method) {
                return message;
            }
        }

        static class MediaGroupPostSender implements PostSender<SendMediaGroup, List<Message>> {
            private final List<Message> messages;

            MediaGroupPostSender(List<Message> messages) {
                this.messages = messages;
            }

            @Override
            public List<Message> send(SendMediaGroup method) {
                return messages;
            }
        }
    }

    @Nested
    class AdviceDispatchTest {
        @Test
        void shouldSendPostControlKeyboardWhenPostIsMessage() throws TelegramApiException {
            stubPostControlsSendMessage();

            postControlsSender.sendPostControlKeyboard((Object) message);

            verify(telegramClient).execute(sendMessage);
        }

        @Test
        void shouldSendPostControlKeyboardWhenPostIsListOfMessages() throws TelegramApiException {
            stubPostControlsSendMessage();

            postControlsSender.sendPostControlKeyboard((Object) List.of(message));

            verify(telegramClient).execute(sendMessage);
        }

        @Test
        void shouldIgnoreNonMessagePostListElements() throws TelegramApiException {
            var post = List.of(message, "not a message");

            stubPostControlsSendMessage();

            postControlsSender.sendPostControlKeyboard((Object) post);

            verify(sendMessageMapper).map(postControlsSendMessageProperties, List.of(message));
            verify(telegramClient).execute(sendMessage);
        }

        @Test
        void shouldIgnorePostListWithoutMessages() {
            postControlsSender.sendPostControlKeyboard((Object) List.of("not a message"));

            verifyNoInteractions(telegramClient, sendMessageMapper);
        }

        @Test
        void shouldIgnoreUnknownPost() {
            postControlsSender.sendPostControlKeyboard("unexpected post");

            verifyNoInteractions(telegramClient, sendMessageMapper);
        }

        @Test
        void shouldIgnoreNullPost() {
            postControlsSender.sendPostControlKeyboard((Object) null);

            verifyNoInteractions(telegramClient, sendMessageMapper);
        }

        @Test
        void shouldHandleTelegramApiException() throws TelegramApiException {
            when(telegramClient.execute(sendMessage)).thenThrow(TelegramApiException.class);
            stubPostControlsSendMessage();

            assertDoesNotThrow(() -> postControlsSender.sendPostControlKeyboard(List.of(message)));
        }
    }
}
