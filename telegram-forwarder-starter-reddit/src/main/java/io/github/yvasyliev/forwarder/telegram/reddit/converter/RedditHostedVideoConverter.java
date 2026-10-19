package io.github.yvasyliev.forwarder.telegram.reddit.converter;

import io.github.yvasyliev.forwarder.telegram.core.dto.PartialBotApiMethodDTO;
import io.github.yvasyliev.forwarder.telegram.core.util.CloseableSupplier;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import io.github.yvasyliev.forwarder.telegram.reddit.mapper.RedditSendVideoDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Converts a Reddit post containing a hosted video into a {@link PartialBotApiMethodDTO} for sending a video.
 */
@Component
@RequiredArgsConstructor
public class RedditHostedVideoConverter implements RedditSingleLinkConverter {
    private final RedditSendVideoDTOMapper sendVideoDTOMapper;

    /**
     * {@inheritDoc}
     *
     * @param post {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Optional<CloseableSupplier<PartialBotApiMethodDTO>> convert(Link post) {
        return Link.PostHint.HOSTED_VIDEO.equals(post.postHint())
                ? Optional.of(() -> sendVideoDTOMapper.map(post))
                : Optional.empty();
    }
}
