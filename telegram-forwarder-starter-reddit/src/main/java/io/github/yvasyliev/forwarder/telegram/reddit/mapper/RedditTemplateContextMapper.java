package io.github.yvasyliev.forwarder.telegram.reddit.mapper;

import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import io.github.yvasyliev.forwarder.telegram.thymeleaf.TemplateContext;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting Reddit {@link Link} objects to {@link TemplateContext} objects.
 */
@Mapper(uses = RedditContextMapper.class)
public interface RedditTemplateContextMapper {
    /**
     * Maps a Reddit {@link Link} object to a {@link TemplateContext} object.
     *
     * @param post the Reddit {@link Link} object to be mapped
     * @return the corresponding {@link TemplateContext} object
     */
    @Mapping(target = "template", constant = "url")
    @Mapping(target = "context", source = ".")
    TemplateContext map(Link post);
}
