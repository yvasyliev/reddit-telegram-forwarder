package io.github.yvasyliev.forwarder.telegram.reddit.mapper;

import io.github.yvasyliev.forwarder.telegram.core.dto.SendMessageDTO;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import io.github.yvasyliev.forwarder.telegram.thymeleaf.TelegramTemplateProcessor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting Reddit {@link Link} objects to {@link SendMessageDTO} objects.
 */
@Mapper(uses = {RedditTemplateContextMapper.class, TelegramTemplateProcessor.class})
public interface RedditSendMessageDTOMapper {
    /**
     * Maps a Reddit {@link Link} object to a {@link SendMessageDTO} object.
     *
     * @param post the Reddit {@link Link} object to be mapped
     * @return the corresponding {@link SendMessageDTO} object
     */
    @Mapping(target = "text", source = "post")
    SendMessageDTO map(Link post);
}
