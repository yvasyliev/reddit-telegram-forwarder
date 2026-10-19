package io.github.yvasyliev.forwarder.telegram.reddit.mapper;

import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

/**
 * Maps a Reddit {@link Link} to a Thymeleaf {@link IContext}.
 */
@Mapper
public interface RedditContextMapper {
    /**
     * Maps a given {@link Link} to an {@link IContext}.
     *
     * @param post the Reddit post
     * @return the Thymeleaf's {@link IContext}
     */
    @BeanMapping(resultType = Context.class)
    IContext map(Link post);

    /**
     * Sets {@link AbstractContext} variables from a given {@link Link}.
     *
     * @param context the Thymeleaf context
     * @param post    the Reddit post
     */
    @AfterMapping
    default void setVariables(@MappingTarget AbstractContext context, Link post) {
        context.setVariable("text", post.title());
        context.setVariable("url", post.url());
    }
}
