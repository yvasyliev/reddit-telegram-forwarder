package io.github.yvasyliev.forwarder.telegram.reddit.converter;

import io.github.yvasyliev.forwarder.telegram.core.dto.PartialBotApiMethodDTO;
import io.github.yvasyliev.forwarder.telegram.core.util.CloseableSupplier;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import io.github.yvasyliev.forwarder.telegram.reddit.mapper.RedditSendMessageDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Converts a Reddit post containing a link into a {@link PartialBotApiMethodDTO} for sending a message.
 */
@Component
@RequiredArgsConstructor
public class RedditUrlConverter implements RedditSingleLinkConverter {
    private final RedditSendMessageDTOMapper sendMessageDTOMapper;

    /**
     * {@inheritDoc}
     *
     * @param post {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Optional<CloseableSupplier<PartialBotApiMethodDTO>> convert(Link post) {
        return Link.PostHint.LINK.equals(post.postHint())
                ? Optional.of(() -> sendMessageDTOMapper.map(post))
                : Optional.empty();
    }
}
