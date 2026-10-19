package io.github.yvasyliev.forwarder.telegram.reddit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Singular;

import java.util.List;

/**
 * Represents a Reddit listing response.
 *
 * @param after     the name of the last element in the listing, used for pagination
 * @param dist      the number of elements in the listing
 * @param modhash   a hash used to prevent CSRF attacks, can be null
 * @param geoFilter a filter for geographical content, can be null
 * @param children  child elements of the listing, each containing a {@link Thing} with a {@link Link} object
 * @param before    the name of the first element in the listing, used for pagination
 */
@Builder
public record Listing(
        @JsonProperty("after") String after,
        @JsonProperty("dist") Integer dist,
        @JsonProperty("modhash") String modhash,
        @JsonProperty("geo_filter") String geoFilter,
        @JsonProperty("children") @Singular List<Thing<Link>> children,
        @JsonProperty("before") String before
) {}
