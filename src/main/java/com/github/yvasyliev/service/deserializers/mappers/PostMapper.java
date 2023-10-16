package com.github.yvasyliev.service.deserializers.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.yvasyliev.model.dto.Post;
import org.springframework.util.function.ThrowingFunction;

import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@FunctionalInterface
public interface PostMapper extends ThrowingFunction<JsonNode, Optional<Post>> {
    default Stream<JsonNode> stream(Iterator<JsonNode> elements) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(elements, Spliterator.ORDERED), false);
    }
}
