package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.post.PhotoGroupPost;
import com.github.yvasyliev.model.dto.post.Post;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Order(3)
public class PhotoGroupPostMapper extends JsonNodeToPostConverter {
    @Value("10")
    private int pageSize;

    @Override
    public Post convertThrowing(JsonNode jsonPost) {
        if (!jsonPost.has("gallery_data")) {
            return convertNext(jsonPost);
        }

        var photoUrlsPages = extractPhotoUrlsPages(jsonPost);
        var post = new PhotoGroupPost();
        post.setText(title(jsonPost));
        post.setPhotoUrlsPages(photoUrlsPages);
        post.setHasSpoiler(nsfw(jsonPost));
        return post;
    }

    private List<List<String>> extractPhotoUrlsPages(JsonNode post) {
        var photoUrls = extractPhotoUrls(post);
        return List.copyOf(IntStream
                .range(0, photoUrls.size())
                .boxed()
                .collect(Collectors.groupingBy(
                        i -> i / pageSize,
                        Collectors.mapping(photoUrls::get, Collectors.toList())
                ))
                .values());
    }

    private List<String> extractPhotoUrls(JsonNode post) {
        var items = post.requiredAt("/gallery_data/items").elements();
        return stream(items)
                .map(item -> {
                    var mediaId = item.get("media_id").textValue();
                    var metadata = post.get("media_metadata").get(mediaId);
                    return stream(metadata.get("p").elements())
                            .max(Comparator.comparingInt(p -> p.get("x").intValue()));
                })
                .filter(Optional::isPresent)
                .map(optionalP -> optionalP.get().get("u").textValue())
                .toList();
    }
}
