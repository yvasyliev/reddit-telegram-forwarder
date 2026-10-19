package io.github.yvasyliev.forwarder.telegram.reddit.converter;

import io.github.yvasyliev.forwarder.telegram.core.dto.PartialBotApiMethodDTO;
import io.github.yvasyliev.forwarder.telegram.core.util.CloseableSupplier;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Resolves the appropriate {@link RedditLinkConverter} for a given Reddit post and converts it into a list of
 * {@link CloseableSupplier} of {@link PartialBotApiMethodDTO}. If no suitable converter is found, an
 * {@link IllegalArgumentException} is thrown.
 */
@Component
@RequiredArgsConstructor
public class RedditLinkConverterResolver {
    private final List<RedditLinkConverter> converters;

    /**
     * Converts a Reddit post into a list of {@link CloseableSupplier} of {@link PartialBotApiMethodDTO} using the
     * registered converters. If no suitable converter is found, an {@link IllegalArgumentException} is thrown.
     *
     * @param post the Reddit post to be converted
     * @return a list of {@link CloseableSupplier} of {@link PartialBotApiMethodDTO} if the conversion is successful
     * @throws IllegalArgumentException if no suitable converter is found for the given post
     */
    public List<CloseableSupplier<PartialBotApiMethodDTO>> convert(Link post) {
        return converters.stream()
                .map(converter -> converter.convertAll(post))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No suitable converter found for post: " + post));
    }
}
