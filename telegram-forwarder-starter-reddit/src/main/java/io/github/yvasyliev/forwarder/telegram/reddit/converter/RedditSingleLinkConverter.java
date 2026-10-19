package io.github.yvasyliev.forwarder.telegram.reddit.converter;

import io.github.yvasyliev.forwarder.telegram.core.dto.PartialBotApiMethodDTO;
import io.github.yvasyliev.forwarder.telegram.core.util.CloseableSupplier;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;

import java.util.List;
import java.util.Optional;

/**
 * A functional interface for converting a Reddit post into a single {@link PartialBotApiMethodDTO} for sending
 * messages.
 */
@FunctionalInterface
public interface RedditSingleLinkConverter extends RedditLinkConverter {
    /**
     * {@inheritDoc}
     *
     * @param post {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    default Optional<List<CloseableSupplier<PartialBotApiMethodDTO>>> convertAll(Link post) {
        return convert(post).map(List::of);
    }

    /**
     * Converts a Reddit post into an optional {@link CloseableSupplier} of {@link PartialBotApiMethodDTO}.
     *
     * @param post the Reddit post to be converted
     * @return an optional {@link CloseableSupplier} of {@link PartialBotApiMethodDTO} if the conversion is successful,
     *         otherwise an empty optional
     */
    Optional<CloseableSupplier<PartialBotApiMethodDTO>> convert(Link post);
}
