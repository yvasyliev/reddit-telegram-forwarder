package com.github.yvasyliev.exceptions;

import java.io.IOException;

public class VideoUrlParseException extends IOException {
    public VideoUrlParseException(String message) {
        super(message);
    }
}
