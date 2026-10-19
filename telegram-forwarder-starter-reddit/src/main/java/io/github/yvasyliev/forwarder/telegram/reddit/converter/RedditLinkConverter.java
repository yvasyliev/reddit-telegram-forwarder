package io.github.yvasyliev.forwarder.telegram.reddit.converter;

import io.github.yvasyliev.forwarder.telegram.core.dto.PartialBotApiMethodDTO;
import io.github.yvasyliev.forwarder.telegram.core.util.CloseableSupplier;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;

import java.util.List;
import java.util.Optional;

/**
 * A functional interface for converting a Reddit post into a list of {@link PartialBotApiMethodDTO} for sending
 * messages.
 */
@FunctionalInterface
public interface RedditLinkConverter {
    /**
     * Converts a Reddit post into an optional list of {@link CloseableSupplier} of {@link PartialBotApiMethodDTO}.
     *
     * @param post the Reddit post to be converted
     * @return an optional list of {@link CloseableSupplier} of {@link PartialBotApiMethodDTO} if the conversion is
     *         successful, otherwise an empty optional
     */
    Optional<List<CloseableSupplier<PartialBotApiMethodDTO>>> convertAll(Link post);
}
