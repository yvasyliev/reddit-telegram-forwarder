package io.github.yvasyliev.forwarder.telegram.reddit.converter;

import io.github.yvasyliev.forwarder.telegram.core.dto.PartialBotApiMethodDTO;
import io.github.yvasyliev.forwarder.telegram.core.util.CloseableSupplier;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import io.github.yvasyliev.forwarder.telegram.reddit.mapper.RedditSendAnimationDTOMapper;
import io.github.yvasyliev.forwarder.telegram.reddit.mapper.RedditSendPhotoDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Converts a Reddit post containing an image into a {@link PartialBotApiMethodDTO} for sending a photo or animation.
 */
@Component
@RequiredArgsConstructor
public class RedditImageConverter implements RedditSingleLinkConverter {
    private final RedditSendAnimationDTOMapper sendAnimationDTOMapper;
    private final RedditSendPhotoDTOMapper sendPhotoDTOMapper;

    private static boolean isGif(Link post) {
        return post.preview()
                .images()
                .getFirst()
                .variants()
                .hasGif();
    }

    /**
     * {@inheritDoc}
     *
     * @param post {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Optional<CloseableSupplier<PartialBotApiMethodDTO>> convert(Link post) {
        return Link.PostHint.IMAGE.equals(post.postHint())
                ? Optional.of(convertImage(post))
                : Optional.empty();
    }

    private CloseableSupplier<PartialBotApiMethodDTO> convertImage(Link post) {
        return () -> isGif(post)
                ? sendAnimationDTOMapper.map(post)
                : sendPhotoDTOMapper.map(post);
    }
}
