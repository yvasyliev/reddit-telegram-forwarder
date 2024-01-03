package com.github.yvasyliev.service.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.util.Converter;
import com.github.yvasyliev.exceptions.ConverterException;
import com.github.yvasyliev.model.dto.post.Post;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PostDelegatingDeserializer extends StdDelegatingDeserializer<Post> {
    public PostDelegatingDeserializer(Converter<?, Post> jsonNodeToPostConverter) {
        super(jsonNodeToPostConverter);
    }

    @Override
    protected StdDelegatingDeserializer<Post> withDelegate(Converter<Object, Post> converter, JavaType delegateType, JsonDeserializer<?> delegateDeserializer) {
        return new StdDelegatingDeserializer<>(converter, delegateType, delegateDeserializer);
    }

    @Override
    public Post deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            var post = super.deserialize(p, ctxt);
            if (post == null) {
                throw new JsonMappingException(p, "No converter found for the post.");
            }
            return post;
        } catch (ConverterException e) {
            throw new JsonMappingException(p, "Failed to convert post.", e.getCause());
        }
    }
}
