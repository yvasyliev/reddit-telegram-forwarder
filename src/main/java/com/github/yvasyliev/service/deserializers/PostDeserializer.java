package com.github.yvasyliev.service.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.entity.Post;
import com.github.yvasyliev.service.dao.BlockedAuthorService;
import com.github.yvasyliev.service.deserializers.mappers.PostMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PostDeserializer extends JsonDeserializer<Post> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostDeserializer.class);
    @Autowired
    private List<PostMapper> postMappers;

    @Autowired
    private BlockedAuthorService blockedAuthorService;

    @Override
    public Post deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        var node = jsonParser.getCodec().readTree(jsonParser);
        if (node instanceof JsonNode jsonPost) {
            var author = jsonPost.get("author").textValue();
            var hasSpoiler = "nsfw".equals(jsonPost.get("thumbnail").textValue());
            var created = jsonPost.get("created").intValue();
            var postUrl = jsonPost.get("url_overridden_by_dest").textValue();

            jsonPost = extractRootPost(jsonPost);
            for (PostMapper postMapper : postMappers) {
                var post = postMapper.apply(jsonPost);
                if (post != null) {
                    post.setAuthor(author);
                    post.setHasSpoiler(hasSpoiler);
                    post.setCreated(created);
                    post.setApproved(blockedAuthorService.isBlocked(author));
                    post.setPostUrl(postUrl);
                    return post;
                }
            }
        }

        LOGGER.error("Failed to parse post: {}", node);
        return null;
    }

    private JsonNode extractRootPost(JsonNode jsonPost) {
        return jsonPost.has("crosspost_parent_list")
                ? extractRootPost(jsonPost.get("crosspost_parent_list").get(0))
                : jsonPost;
    }
}
