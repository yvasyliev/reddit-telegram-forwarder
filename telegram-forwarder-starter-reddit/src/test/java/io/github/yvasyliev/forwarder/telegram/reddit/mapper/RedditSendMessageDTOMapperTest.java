package io.github.yvasyliev.forwarder.telegram.reddit.mapper;

import io.github.yvasyliev.forwarder.telegram.core.dto.SendMessageDTO;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import io.github.yvasyliev.forwarder.telegram.thymeleaf.TelegramTemplateProcessor;
import io.github.yvasyliev.forwarder.telegram.thymeleaf.TemplateContext;
import lombok.Cleanup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedditSendMessageDTOMapperTest {
    @InjectMocks private RedditSendMessageDTOMapperImpl sendMessageDTOMapper;
    @Mock private RedditTemplateContextMapper redditTemplateContextMapper;
    @Mock private TelegramTemplateProcessor telegramTemplateProcessor;

    @Test
    void shouldReturnNull() throws IOException {
        @Cleanup var actual = sendMessageDTOMapper.map(null);

        assertNull(actual);
        verifyNoInteractions(redditTemplateContextMapper, telegramTemplateProcessor);
    }

    @Test
    void testMap() throws IOException {
        var post = mock(Link.class);
        var templateContext = mock(TemplateContext.class);
        var text = "processed text";
        @Cleanup var expected = new SendMessageDTO(text);

        when(redditTemplateContextMapper.map(post)).thenReturn(templateContext);
        when(telegramTemplateProcessor.process(templateContext)).thenReturn(text);

        @Cleanup var actual = sendMessageDTOMapper.map(post);

        assertEquals(expected, actual);
    }
}
