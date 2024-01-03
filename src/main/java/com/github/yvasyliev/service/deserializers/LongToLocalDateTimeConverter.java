package com.github.yvasyliev.service.deserializers;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class LongToLocalDateTimeConverter extends StdConverter<Long, LocalDateTime> {
    @Override
    public LocalDateTime convert(Long millis) {
        return LocalDateTime.now().plus(millis, ChronoUnit.MILLIS);
    }
}
