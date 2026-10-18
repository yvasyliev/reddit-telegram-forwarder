package io.github.yvasyliev.forwarder.telegram.core.dto;

import java.net.URL;

/**
 * DTO for sending a URL with optional text.
 *
 * @param url  the URL to be sent
 * @param text the optional accompanying text
 * @deprecated use {@link SendMessageDTO} instead
 */
@Deprecated(forRemoval = true)
public record SendUrlDTO(URL url, String text) {}
