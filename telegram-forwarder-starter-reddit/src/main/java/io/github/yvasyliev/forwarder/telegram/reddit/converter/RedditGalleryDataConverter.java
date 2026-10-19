package io.github.yvasyliev.forwarder.telegram.reddit.converter;

import io.github.yvasyliev.forwarder.telegram.core.dto.PartialBotApiMethodDTO;
import io.github.yvasyliev.forwarder.telegram.core.util.CloseableSupplier;
import io.github.yvasyliev.forwarder.telegram.reddit.dto.Link;
import io.github.yvasyliev.forwarder.telegram.reddit.mapper.RedditSendAnimationDTOMapper;
import io.github.yvasyliev.forwarder.telegram.reddit.mapper.RedditSendMediaGroupDTOMapper;
import io.github.yvasyliev.forwarder.telegram.reddit.mapper.RedditSendPhotoDTOMapper;
import io.github.yvasyliev.forwarder.telegram.reddit.util.GalleryMetadataPartitioner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Converts a Reddit post containing gallery data into a list of {@link PartialBotApiMethodDTO} for sending messages.
 */
@Component
@RequiredArgsConstructor
public class RedditGalleryDataConverter implements RedditLinkConverter {
    private final GalleryMetadataPartitioner galleryMetadataPartitioner;
    private final RedditSendAnimationDTOMapper sendAnimationDTOMapper;
    private final RedditSendPhotoDTOMapper sendPhotoDTOMapper;
    private final RedditSendMediaGroupDTOMapper sendMediaGroupDTOMapper;

    /**
     * {@inheritDoc}
     *
     * @param post {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Optional<List<CloseableSupplier<PartialBotApiMethodDTO>>> convertAll(Link post) {
        return post.hasGalleryData()
                ? Optional.of(toSuppliers(post))
                : Optional.empty();
    }

    private List<CloseableSupplier<PartialBotApiMethodDTO>> toSuppliers(Link post) {
        var caption = post.title();
        var hasSpoiler = post.isNsfw();
        var partitions = galleryMetadataPartitioner.partition(post);

        return IntStream.range(0, partitions.size())
                .mapToObj(i -> convertPartition(partitions.get(i), hasSpoiler, i == 0 ? caption : null))
                .toList();
    }

    private CloseableSupplier<PartialBotApiMethodDTO> convertPartition(
            List<Link.Metadata> metadataPartition,
            boolean hasSpoiler,
            String caption
    ) {
        return () -> metadataPartition.size() == 1
                ? convertSingleMedia(metadataPartition.getFirst(), hasSpoiler)
                : sendMediaGroupDTOMapper.map(metadataPartition, hasSpoiler, caption);
    }

    private PartialBotApiMethodDTO convertSingleMedia(Link.Metadata metadata, boolean hasSpoiler) throws IOException {
        return switch (metadata.type()) {
            case ANIMATED_IMAGE -> sendAnimationDTOMapper.map(metadata, hasSpoiler);
            case IMAGE -> sendPhotoDTOMapper.map(metadata, hasSpoiler);
        };
    }
}
