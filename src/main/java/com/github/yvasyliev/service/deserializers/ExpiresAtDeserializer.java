package com.github.yvasyliev.service.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ExpiresAtDeserializer extends StdDeserializer<LocalDateTime> {
    protected ExpiresAtDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return LocalDateTime.now().plus(jsonParser.getLongValue(), ChronoUnit.MILLIS);
    }
}
