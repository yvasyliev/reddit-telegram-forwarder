package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.github.yvasyliev.exceptions.ConverterException;
import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.service.data.BlockedAuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class JsonNodeToPostConverter extends StdConverter<JsonNode, Post> {
    @Autowired
    private BlockedAuthorService blockedAuthorService;

    @Value("${reddit.authors.blocked.by.default:false}")
    private boolean authorsBlockedByDefault;

    private JsonNodeToPostConverter nextConverter;

    public static JsonNodeToPostConverter createChain(JsonNodeToPostConverter firstConverter, JsonNodeToPostConverter... chain) {
        var head = firstConverter;
        for (JsonNodeToPostConverter nextInChain : chain) {
            head.nextConverter = nextInChain;
            head = nextInChain;
        }
        return firstConverter;
    }

    @Override
    public Post convert(JsonNode jsonPost) {
        try {
            var post = convertThrowing(extractRootPost(jsonPost));
            post.setAuthor(jsonPost.get("author").textValue());
            post.setCreated(jsonPost.get("created").intValue());
            post.setApproved(!authorsBlockedByDefault && !blockedAuthorService.isBlocked(post.getAuthor()));
            post.setPostUrl(jsonPost.path("url_overridden_by_dest").asText(jsonPost.get("url").textValue()));
            return post;
        } catch (Exception e) {
            throw new ConverterException(e);
        }
    }

    public abstract Post convertThrowing(JsonNode jsonPost) throws Exception;

    protected Post convertNext(JsonNode jsonPost) {
        return nextConverter != null ? nextConverter.convert(jsonPost) : null;
    }

    protected Stream<JsonNode> stream(Iterator<JsonNode> elements) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(elements, Spliterator.ORDERED), false);
    }

    protected String title(JsonNode jsonPost) {
        return jsonPost.get("title").textValue();
    }

    protected boolean nsfw(JsonNode jsonPost) {
        return "nsfw".equals(jsonPost.get("thumbnail").textValue());
    }

    private JsonNode extractRootPost(JsonNode jsonPost) {
        return jsonPost.has("crosspost_parent_list")
                ? extractRootPost(jsonPost.at("/crosspost_parent_list/0"))
                : jsonPost;
    }
}
