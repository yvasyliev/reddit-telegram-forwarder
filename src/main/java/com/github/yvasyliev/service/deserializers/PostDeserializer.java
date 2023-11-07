package com.github.yvasyliev.service.deserializers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.model.entities.BlockedAuthor;
import com.github.yvasyliev.service.data.BlockedAuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingFunction;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PostDeserializer extends JsonDeserializer<Post> {
    @Autowired
    private List<ThrowingFunction<JsonNode, Optional<Post>>> postMappers;

    @Autowired
    private BlockedAuthorService blockedAuthorService;

    @Override
    public Post deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        var jsonPost = jsonParser.readValueAs(JsonNode.class);
        var author = jsonPost.get("author").textValue();
        var created = jsonPost.get("created").intValue();
        var postUrl = jsonPost.has("url_overridden_by_dest") ? jsonPost.get("url_overridden_by_dest").textValue() : jsonPost.get("url").textValue();
        var blockedAuthors = blockedAuthorService
                .findAll()
                .stream()
                .map(BlockedAuthor::getUsername)
                .toList();

        jsonPost = extractRootPost(jsonPost);
        for (var postMapper : postMappers) {
            try {
                var optionalPost = postMapper.applyWithException(jsonPost).map(post -> {
                    post.setAuthor(author);
                    post.setCreated(created);
                    post.setApproved(!blockedAuthors.contains(author));
                    post.setPostUrl(postUrl);
                    return post;
                });
                if (optionalPost.isPresent()) {
                    return optionalPost.get();
                }
            } catch (Exception e) {
                throw new JsonParseException(jsonParser, e.getMessage(), e);
            }
        }

        throw new JsonMappingException(jsonParser, "Failed to parse post: " + jsonPost);
    }

    private JsonNode extractRootPost(JsonNode jsonPost) {
        return jsonPost.has("crosspost_parent_list")
                ? extractRootPost(jsonPost.at("/crosspost_parent_list/0"))
                : jsonPost;
    }
}
