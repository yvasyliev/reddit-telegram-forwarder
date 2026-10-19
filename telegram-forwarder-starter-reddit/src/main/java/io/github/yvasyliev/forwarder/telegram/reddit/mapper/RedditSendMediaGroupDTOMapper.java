package io.github.yvasyliev.forwarder.telegram.reddit.mapper;

import io.github.yvasyliev.forwarder.telegram.core.dto.InputMediaDTO;
import io.github.yvasyliev.forwarder.telegram.core.dto.SendMediaGroupDTO;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import lombok.Setter;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

/**
 * {@link SendMediaGroupDTO} mapper.
 */
@Mapper
@Setter(onMethod_ = @Autowired)
@SuppressWarnings("checkstyle:AbstractClassName")
public abstract class RedditSendMediaGroupDTOMapper {
    private RedditInputMediaVideoDTOMapper inputMediaVideoDTOMapper;
    private RedditInputMediaPhotoDTOMapper inputMediaPhotoDTOMapper;

    /**
     * Maps a list of {@link Link.Metadata} to {@link SendMediaGroupDTO}.
     *
     * @param medias     the list of Reddit link metadata
     * @param hasSpoiler whether the media has a spoiler
     * @param caption    the caption for the media group
     * @return the mapped {@link SendMediaGroupDTO}
     * @throws IOException if an I/O error occurs during mapping
     */
    public abstract SendMediaGroupDTO map(List<Link.Metadata> medias, @Context boolean hasSpoiler, String caption)
            throws IOException;

    protected InputMediaDTO map(Link.Metadata metadata, @Context boolean hasSpoiler) throws IOException {
        return switch (metadata.type()) {
            case ANIMATED_IMAGE -> inputMediaVideoDTOMapper.map(metadata, hasSpoiler);
            case IMAGE -> inputMediaPhotoDTOMapper.map(metadata, hasSpoiler);
        };
    }
}
