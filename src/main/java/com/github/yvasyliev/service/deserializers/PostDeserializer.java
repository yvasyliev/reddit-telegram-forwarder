package com.github.yvasyliev.service.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.Post;
import com.github.yvasyliev.service.deserializers.mappers.PostMapper;
import com.github.yvasyliev.service.json.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PostDeserializer extends JsonDeserializer<Post> {
    @Autowired
    private List<PostMapper> postMappers;

    @Autowired
    private ApplicationContext context;

    @Override
    public Post deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        var node = jsonParser.getCodec().readTree(jsonParser);
        if (node instanceof JsonNode jsonPost) {
            var author = jsonPost.get("author").textValue();
            var hasSpoiler = "nsfw".equals(jsonPost.get("thumbnail").textValue());
            var created = jsonPost.get("created").intValue();
            var postUrl = jsonPost.get("url_overridden_by_dest").textValue();
            var state = context.getBean("state", State.class);

            jsonPost = extractRootPost(jsonPost);
            for (PostMapper postMapper : postMappers) {
                var post = postMapper.apply(jsonPost);
                if (post != null) {
                    post.setAuthor(author);
                    post.setHasSpoiler(hasSpoiler);
                    post.setCreated(created);
                    post.setApproved(state.getBlockedAuthors().contains(author));
                    post.setPostUrl(postUrl);
                    return post;
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
