package io.github.yvasyliev.forwarder.telegram.reddit.converter;

import io.github.yvasyliev.forwarder.telegram.core.dto.PartialBotApiMethodDTO;
import io.github.yvasyliev.forwarder.telegram.core.util.CloseableSupplier;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import io.github.yvasyliev.forwarder.telegram.reddit.mapper.RedditSendAnimationDTOMapper;
import io.github.yvasyliev.forwarder.telegram.reddit.mapper.RedditSendMessageDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Converts a Reddit post containing a rich video into a {@link PartialBotApiMethodDTO} for sending a message or
 * animation.
 */
@Component
@RequiredArgsConstructor
public class RedditRichVideoConverter implements RedditSingleLinkConverter {
    private final RedditSendAnimationDTOMapper sendAnimationDTOMapper;
    private final RedditSendMessageDTOMapper sendMessageDTOMapper;

    private static boolean isGif(Link post) {
        var redditVideo = post.preview().redditVideoPreview();

        return redditVideo != null && redditVideo.isGif();
    }

    private static boolean isAnimation(Link post) {
        return Boolean.TRUE.equals(post.isRedditMediaDomain()) && isGif(post);
    }

    /**
     * {@inheritDoc}
     *
     * @param post {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Optional<CloseableSupplier<PartialBotApiMethodDTO>> convert(Link post) {
        return Link.PostHint.RICH_VIDEO.equals(post.postHint())
                ? Optional.of(convertRichVideo(post))
                : Optional.empty();
    }

    private CloseableSupplier<PartialBotApiMethodDTO> convertRichVideo(Link post) {
        return () -> isAnimation(post)
                ? sendAnimationDTOMapper.map(post)
                : sendMessageDTOMapper.map(post);
    }
}
