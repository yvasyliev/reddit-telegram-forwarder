package com.github.yvasyliev.service.deserializers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.service.state.StateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
    private ApplicationContext context;

    @Override
    public Post deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        var node = jsonParser.getCodec().readTree(jsonParser);
        if (node instanceof JsonNode jsonPost) {
            var author = jsonPost.get("author").textValue();
            var created = jsonPost.get("created").intValue();
            var postUrl = jsonPost.get("url_overridden_by_dest").textValue();
            var blockedAuthors = context.getBean(StateManager.class).getBlockedAuthors();

            jsonPost = extractRootPost(jsonPost);
            for (var postMapper : postMappers) {
                try {
                    var optionalPost = postMapper.applyWithException(jsonPost).map(post -> {
                        post.setAuthor(author);
                        post.setCreated(created);
                        post.setApproved(blockedAuthors.contains(author));
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
        }

        throw new JsonMappingException(jsonParser, "Failed to parse post: " + node);
    }

    private JsonNode extractRootPost(JsonNode jsonPost) {
        return jsonPost.has("crosspost_parent_list")
                ? extractRootPost(jsonPost.get("crosspost_parent_list").get(0))
                : jsonPost;
    }
}
