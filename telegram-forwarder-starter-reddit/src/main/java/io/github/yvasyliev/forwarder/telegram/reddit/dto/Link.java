package io.github.yvasyliev.forwarder.telegram.reddit.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.yvasyliev.forwarder.telegram.reddit.deser.EditedDeserializer;
import io.github.yvasyliev.forwarder.telegram.reddit.deser.PermalinkDeserializer;
import lombok.Builder;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.util.CollectionUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.node.ArrayNode;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents a Reddit link (post) with various attributes.
 *
 * @param approvedAtUtc              the time when the post was approved in UTC
 * @param subreddit                  the subreddit where the post was made
 * @param selftext                   the text content of the post
 * @param authorFullname             the full name of the author
 * @param saved                      indicates if the post is saved by the user
 * @param modReasonTitle             the title of the moderation reason
 * @param gilded                     the number of times the post has been gilded
 * @param clicked                    indicates if the post has been clicked
 * @param title                      the title of the post
 * @param linkFlairRichtext          the rich text formatting of the link flair
 * @param subredditNamePrefixed      the subreddit name prefixed with "r/"
 * @param hidden                     indicates if the post is hidden
 * @param pwls                       the post's whitelist status
 * @param linkFlairCssClass          the CSS class for the link flair
 * @param downs                      the number of downvotes the post has received
 * @param thumbnailHeight            the height of the thumbnail image
 * @param topAwardedType             the type of the top awarded post
 * @param hideScore                  indicates if the score of the post is hidden
 * @param mediaMetadata              the metadata for media associated with the post
 * @param name                       the name of the post
 * @param quarantine                 indicates if the post is quarantined
 * @param linkFlairTextColor         the text color of the link flair
 * @param upvoteRatio                the ratio of upvotes to total votes for the post
 * @param authorFlairBackgroundColor the background color of the author's flair
 * @param subredditType              the type of subreddit (e.g., public, private)
 * @param ups                        the number of upvotes the post has received
 * @param totalAwardsReceived        the total number of awards received by the post
 * @param mediaEmbed                 the media embed information for the post
 * @param thumbnailWidth             the width of the thumbnail image
 * @param authorFlairTemplateId      the template ID for the author's flair
 * @param isOriginalContent          indicates if the post is original content
 * @param userReports                the reports made by users against the post
 * @param secureMedia                the secure media associated with the post
 * @param isRedditMediaDomain        indicates if the post is from a Reddit media domain
 * @param isMeta                     indicates if the post is a meta post
 * @param category                   the category of the post
 * @param secureMediaEmbed           the secure media embed information for the post
 * @param galleryData                the gallery data associated with the post
 * @param linkFlairText              the text of the link flair
 * @param canModPost                 indicates if the user can moderate the post
 * @param score                      the score of the post (upvotes - downvotes)
 * @param approvedBy                 the user who approved the post, if applicable
 * @param isCreatedFromAdsUi         indicates if the post was created from the ads UI
 * @param authorPremium              indicates if the author has a premium account
 * @param thumbnail                  the URL of the thumbnail image for the post
 * @param edited                     indicates if the post has been edited
 * @param authorFlairCssClass        the CSS class for the author's flair
 * @param authorFlairRichtext        the rich text formatting of the author's flair
 * @param gildings                   the gildings associated with the post
 * @param postHint                   the hint for the type of post (e.g., image, video)
 * @param contentCategories          the content categories associated with the post
 * @param isSelf                     indicates if the post is a self-post (text only)
 * @param modNote                    the moderation note for the post, if applicable
 * @param crosspostParentList        the list of crosspost parents for the post
 * @param created                    the creation time of the post
 * @param linkFlairType              the type of link flair (e.g., text, richtext)
 * @param wls                        the whitelist status of the post
 * @param removedByCategory          the category of the user who removed the post, if applicable
 * @param bannedBy                   the user who banned the post, if applicable
 * @param authorFlairType            the type of author's flair (e.g., text, richtext)
 * @param domain                     the domain of the post (e.g., reddit.com, imgur.com)
 * @param allowLiveComments          indicates if live comments are allowed on the post
 * @param selftextHtml               the HTML content of the selftext
 * @param likes                      indicates if the user likes the post
 * @param suggestedSort              the suggested sort order for the post
 * @param bannedAtUtc                the time when the post was banned in UTC
 * @param urlOverriddenByDest        the URL that overrides the destination URL of the post
 * @param viewCount                  the number of views the post has received
 * @param archived                   indicates if the post is archived
 * @param noFollow                   indicates if the post should not be followed by search engines
 * @param isCrosspostable            indicates if the post can be crossposted
 * @param pinned                     indicates if the post is pinned to the top of the subreddit
 * @param over18                     indicates if the post is marked as NSFW (not safe for work)
 * @param preview                    the preview data for the post, including images and video
 * @param allAwardings               the list of all awards received by the post
 * @param awarders                   the list of users who awarded the post
 * @param mediaOnly                  indicates if the post contains only media content
 * @param canGild                    indicates if the post can be gilded
 * @param spoiler                    indicates if the post contains a spoiler
 * @param locked                     indicates if the post is locked for comments
 * @param authorFlairText            the text of the author's flair
 * @param treatmentTags              the treatment tags associated with the post
 * @param visited                    indicates if the post has been visited by the user
 * @param removedBy                  the user who removed the post, if applicable
 * @param numReports                 the number of reports made against the post
 * @param distinguished              the distinction status of the post (e.g., admin, moderator)
 * @param subredditId                the ID of the subreddit where the post was made
 * @param authorIsBlocked            indicates if the author is blocked by the user
 * @param modReasonBy                the user who provided the moderation reason, if applicable
 * @param removalReason              the reason for the removal of the post, if applicable
 * @param linkFlairBackgroundColor   the background color of the link flair
 * @param id                         the unique identifier of the post
 * @param isRobotIndexable           indicates if the post is indexable by robots
 * @param reportReasons              the reasons for reporting the post, if applicable
 * @param author                     the username of the author of the post
 * @param discussionType             the type of discussion for the post (e.g., chat, Q{@literal &}A)
 * @param numComments                the number of comments on the post
 * @param sendReplies                indicates if replies to the post should be sent
 * @param whitelistStatus            the whitelist status of the post
 * @param contestMode                indicates if the post is in contest mode
 * @param pollData                   the poll data associated with the post, if any
 * @param modReports                 the reports made by moderators against the post
 * @param authorPatreonFlair         indicates if the author has a Patreon flair
 * @param authorFlairTextColor       the text color of the author's flair
 * @param permalink                  the permalink to the post, deserialized to a URL
 * @param parentWhitelistStatus      the whitelist status of the parent post, if applicable
 * @param stickied                   indicates if the post is stickied (pinned) in the subreddit
 * @param url                        the URL of the post
 * @param subredditSubscribers       the number of subscribers to the subreddit where the post was made
 * @param createdUtc                 the creation time of the post in UTC
 * @param numCrossposts              the number of crossposts of the post
 * @param media                      the media associated with the post, if any
 * @param isVideo                    indicates if the post is a video
 */
@Builder
public record Link(
        @JsonProperty("approved_at_utc") String approvedAtUtc,
        @JsonProperty("subreddit") String subreddit,
        @JsonProperty("selftext") String selftext,
        @JsonProperty("author_fullname") String authorFullname,
        @JsonProperty("saved") Boolean saved,
        @JsonProperty("mod_reason_title") String modReasonTitle,
        @JsonProperty("gilded") Integer gilded,
        @JsonProperty("clicked") Boolean clicked,
        @JsonProperty("title") String title,
        @JsonProperty("link_flair_richtext") List<FlairRichtext> linkFlairRichtext,
        @JsonProperty("subreddit_name_prefixed") String subredditNamePrefixed,
        @JsonProperty("hidden") Boolean hidden,
        @JsonProperty("pwls") Integer pwls,
        @JsonProperty("link_flair_css_class") String linkFlairCssClass,
        @JsonProperty("downs") Integer downs,
        @JsonProperty("thumbnail_height") Integer thumbnailHeight,
        @JsonProperty("top_awarded_type") String topAwardedType,
        @JsonProperty("hide_score") Boolean hideScore,
        @JsonProperty("media_metadata") Map<String, Metadata> mediaMetadata,
        @JsonProperty("name") String name,
        @JsonProperty("quarantine") Boolean quarantine,
        @JsonProperty("link_flair_text_color") String linkFlairTextColor,
        @JsonProperty("upvote_ratio") Double upvoteRatio,
        @JsonProperty("author_flair_background_color") String authorFlairBackgroundColor,
        @JsonProperty("subreddit_type") String subredditType,
        @JsonProperty("ups") Integer ups,
        @JsonProperty("total_awards_received") Integer totalAwardsReceived,
        @JsonProperty("media_embed") MediaEmbed mediaEmbed,
        @JsonProperty("thumbnail_width") Integer thumbnailWidth,
        @JsonProperty("author_flair_template_id") String authorFlairTemplateId,
        @JsonProperty("is_original_content") Boolean isOriginalContent,
        @JsonProperty("user_reports") ArrayNode userReports,
        @JsonProperty("secure_media") Media secureMedia,
        @JsonProperty("is_reddit_media_domain") Boolean isRedditMediaDomain,
        @JsonProperty("is_meta") Boolean isMeta,
        @JsonProperty("category") String category,
        @JsonProperty("secure_media_embed") MediaEmbed secureMediaEmbed,
        @JsonProperty("gallery_data") GalleryData galleryData,
        @JsonProperty("link_flair_text") String linkFlairText,
        @JsonProperty("can_mod_post") Boolean canModPost,
        @JsonProperty("score") Integer score,
        @JsonProperty("approved_by") String approvedBy,
        @JsonProperty("is_created_from_ads_ui") Boolean isCreatedFromAdsUi,
        @JsonProperty("author_premium") Boolean authorPremium,
        @JsonProperty("thumbnail") String thumbnail,
        @JsonProperty("edited") @JsonDeserialize(using = EditedDeserializer.class) Instant edited,
        @JsonProperty("author_flair_css_class") String authorFlairCssClass,
        @JsonProperty("author_flair_richtext") List<FlairRichtext> authorFlairRichtext,
        @JsonProperty("gildings") Map<String, Integer> gildings,
        @JsonProperty("post_hint") PostHint postHint,
        @JsonProperty("content_categories") List<String> contentCategories,
        @JsonProperty("is_self") Boolean isSelf,
        @JsonProperty("mod_note") String modNote,
        @JsonProperty("crosspost_parent_list") List<Link> crosspostParentList,
        @JsonProperty("created") Instant created,
        @JsonProperty("link_flair_type") String linkFlairType,
        @JsonProperty("wls") Integer wls,
        @JsonProperty("removed_by_category") String removedByCategory,
        @JsonProperty("banned_by") String bannedBy,
        @JsonProperty("author_flair_type") String authorFlairType,
        @JsonProperty("domain") String domain,
        @JsonProperty("allow_live_comments") Boolean allowLiveComments,
        @JsonProperty("selftext_html") String selftextHtml,
        @JsonProperty("likes") Boolean likes,
        @JsonProperty("suggested_sort") String suggestedSort,
        @JsonProperty("banned_at_utc") Instant bannedAtUtc,
        @JsonProperty("url_overridden_by_dest") URL urlOverriddenByDest,
        @JsonProperty("view_count") Integer viewCount,
        @JsonProperty("archived") Boolean archived,
        @JsonProperty("no_follow") Boolean noFollow,
        @JsonProperty("is_crosspostable") Boolean isCrosspostable,
        @JsonProperty("pinned") Boolean pinned,
        @JsonProperty("over_18") Boolean over18,
        @JsonProperty("preview") Preview preview,
        @JsonProperty("all_awardings") List<Awarding> allAwardings,
        @JsonProperty("awarders") ArrayNode awarders,
        @JsonProperty("media_only") Boolean mediaOnly,
        @JsonProperty("can_gild") Boolean canGild,
        @JsonProperty("spoiler") Boolean spoiler,
        @JsonProperty("locked") Boolean locked,
        @JsonProperty("author_flair_text") String authorFlairText,
        @JsonProperty("treatment_tags") ArrayNode treatmentTags,
        @JsonProperty("visited") Boolean visited,
        @JsonProperty("removed_by") String removedBy,
        @JsonProperty("num_reports") Integer numReports,
        @JsonProperty("distinguished") String distinguished,
        @JsonProperty("subreddit_id") String subredditId,
        @JsonProperty("author_is_blocked") Boolean authorIsBlocked,
        @JsonProperty("mod_reason_by") String modReasonBy,
        @JsonProperty("removal_reason") String removalReason,
        @JsonProperty("link_flair_background_color") String linkFlairBackgroundColor,
        @JsonProperty("id") String id,
        @JsonProperty("is_robot_indexable") Boolean isRobotIndexable,
        @JsonProperty("report_reasons") List<String> reportReasons,
        @JsonProperty("author") String author,
        @JsonProperty("discussion_type") String discussionType,
        @JsonProperty("num_comments") Integer numComments,
        @JsonProperty("send_replies") Boolean sendReplies,
        @JsonProperty("whitelist_status") String whitelistStatus,
        @JsonProperty("contest_mode") Boolean contestMode,
        @JsonProperty("poll_data") PollData pollData,
        @JsonProperty("mod_reports") ArrayNode modReports,
        @JsonProperty("author_patreon_flair") Boolean authorPatreonFlair,
        @JsonProperty("author_flair_text_color") String authorFlairTextColor,
        @JsonProperty("permalink") @JsonDeserialize(using = PermalinkDeserializer.class) URL permalink,
        @JsonProperty("parent_whitelist_status") String parentWhitelistStatus,
        @JsonProperty("stickied") Boolean stickied,
        @JsonProperty("url") URL url,
        @JsonProperty("subreddit_subscribers") Integer subredditSubscribers,
        @JsonProperty("created_utc") Instant createdUtc,
        @JsonProperty("num_crossposts") Integer numCrossposts,
        @JsonProperty("media") Media media,
        @JsonProperty("is_video") Boolean isVideo
) implements Comparable<Link> {
    /**
     * Checks if the post is marked as NSFW (not safe for work).
     *
     * @return {@code true} if the post is NSFW, {@code false} otherwise.
     */
    public boolean isNsfw() {
        return "nsfw".equals(thumbnail);
    }

    /**
     * Checks if the post has a gallery associated with it.
     *
     * @return {@code true} if the post has gallery data, {@code false} otherwise.
     */
    public boolean hasGalleryData() {
        return galleryData != null;
    }

    /**
     * Checks if the post has a post hint. This can be used to determine the type of content in the post (e.g., image,
     * video).
     *
     * @return {@code true} if the post has a post hint, {@code false} otherwise.
     */
    public boolean hasPostHint() {
        return postHint != null;
    }

    /**
     * Gets the source link for the post. If the post is a crosspost, it returns the original post; otherwise, it
     * returns the current post.
     *
     * @return the source {@link Link} of the post.
     */
    public Link sourceLink() {
        return ObjectUtils.getIfNull(CollectionUtils.lastElement(crosspostParentList), this);
    }

    @Override
    public int compareTo(Link o) {
        return created.compareTo(o.created);
    }

    /**
     * Represents the hint for the type of post. This can be used to determine how to handle the post (e.g., as an
     * image, video, etc.).
     */
    public enum PostHint {
        /**
         * Represents a post that contains a video hosted on Reddit.
         */
        @JsonProperty("hosted:video") HOSTED_VIDEO,

        /**
         * Represents a post that contains an image.
         */
        @JsonProperty("image") IMAGE,

        /**
         * Represents a post that contains a link to an external resource.
         */
        @JsonProperty("link") LINK,

        /**
         * Represents a post that contains a video hosted on an external platform.
         */
        @JsonProperty("rich:video") RICH_VIDEO,

        /**
         * Represents a post that contains a gallery of images.
         */
        @JsonProperty("gallery") GALLERY
    }

    /**
     * Represents the preview data for a Reddit post.
     *
     * @param images             the list of images in the preview
     * @param enabled            indicates if the preview is enabled
     * @param redditVideoPreview the Reddit video preview, if available
     */
    @Builder
    public record Preview(
            @JsonProperty("images") List<Metadata> images,
            @JsonProperty("enabled") Boolean enabled,
            @JsonProperty("reddit_video_preview") RedditVideo redditVideoPreview
    ) {}

    /**
     * Represents a resolution of an image or video.
     *
     * @param url    the URL of the image or video
     * @param width  the width of the image or video
     * @param height the height of the image or video
     * @param gif    the GIF variant, if available
     * @param mp4    the MP4 variant, if available
     */
    public record Resolution(
            @JsonProperty("url") @JsonAlias("u") URL url,
            @JsonProperty("width") @JsonAlias("x") Integer width,
            @JsonProperty("height") @JsonAlias("y") Integer height,
            @JsonProperty("gif") URL gif,
            @JsonProperty("mp4") URL mp4
    ) implements Comparable<Resolution> {
        @Override
        public int compareTo(Resolution resolution) {
            return Integer.compare(width * height, resolution.width * resolution.height);
        }
    }

    /**
     * Represents the variants of a post's media.
     *
     * @param gif        the GIF variant, if available
     * @param mp3        the MP3 variant, if available
     * @param mp4        the MP4 variant, if available
     * @param nsfw       the NSFW variant, if available
     * @param obfuscated the obfuscated variant, if available
     */
    @Builder
    public record Variants(
            @JsonProperty("gif") Variant gif,
            @JsonProperty("mp3") Variant mp3,
            @JsonProperty("mp4") Variant mp4,
            @JsonProperty("nsfw") Variant nsfw,
            @JsonProperty("obfuscated") Variant obfuscated
    ) {
        /**
         * Checks if the post has a GIF variant.
         *
         * @return {@code true} if the post has a GIF variant, {@code false} otherwise.
         */
        public boolean hasGif() {
            return gif != null;
        }
    }

    /**
     * Represents a variant of a post's media with its source resolution and available resolutions.
     *
     * @param source      the source resolution of the variant
     * @param resolutions the list of resolutions available for the variant
     */
    @Builder
    public record Variant(
            @JsonProperty("source") Resolution source,
            @JsonProperty("resolutions") List<Resolution> resolutions
    ) {}

    /**
     * Represents the gallery data associated with a Reddit post.
     *
     * @param items the list of items in the gallery
     */
    public record GalleryData(@JsonProperty("items") List<Item> items) {
        /**
         * Represents an item in the gallery.
         *
         * @param mediaId the media ID of the item
         * @param id      the unique identifier of the item
         */
        public record Item(@JsonProperty("media_id") String mediaId, @JsonProperty("id") Integer id) {}
    }

    /**
     * Represents metadata for a Reddit post, including its status, type, format, resolutions, source resolution, and
     * ID.
     *
     * @param status      the status of the metadata (e.g., valid)
     * @param type        the type of the post (e.g., image, animated image)
     * @param format      the format of the post (e.g., image/gif, image/jpg, image/png)
     * @param resolutions the list of resolutions available for the post
     * @param source      the source resolution of the post
     * @param id          the unique identifier of the post
     * @param variants    the variants of the post's media (e.g., GIF, MP4)
     */
    @Builder
    public record Metadata(
            @JsonProperty("status") Status status,
            @JsonProperty("e") Type type,
            @JsonProperty("m") Format format,
            @JsonProperty("resolutions") @JsonAlias("p") List<Resolution> resolutions,
            @JsonProperty("source") @JsonAlias("s") Resolution source,
            @JsonProperty("id") String id,
            @JsonProperty("variants") Variants variants
    ) {
        /**
         * Represents the status of the metadata. This can be used to determine if the metadata is valid or not.
         */
        public enum Status {
            /**
             * Represents a valid metadata status.
             */
            @JsonProperty("valid") VALID,

            /**
             * Represents an unprocessed metadata status.
             */
            @JsonProperty("unprocessed") UNPROCESSED
        }

        /**
         * Represents the type of the post's media. This can be used to determine if the post is an image or an animated
         * image.
         */
        public enum Type {
            /**
             * Represents a standard image.
             */
            @JsonProperty("Image") IMAGE,

            /**
             * Represents an animated image (e.g., GIF).
             */
            @JsonProperty("AnimatedImage") ANIMATED_IMAGE
        }

        /**
         * Represents the format of the post's media. This can be used to determine the MIME type of the media (e.g.,
         * image/gif, image/jpg, image/png).
         */
        public enum Format {
            /**
             * Represents a GIF image format.
             */
            @JsonProperty("image/gif") IMAGE_GIF,

            /**
             * Represents a JPEG image format.
             */
            @JsonProperty("image/jpg") IMAGE_JPG,

            /**
             * Represents a JPEG image format.
             */
            @JsonProperty("image/jpeg") IMAGE_JPEG,

            /**
             * Represents a PNG image format.
             */
            @JsonProperty("image/png") IMAGE_PNG
        }
    }

    /**
     * Represents the data for a Reddit poll.
     *
     * @param predictionStatus    the status of the prediction
     * @param tournamentId        the ID of the tournament
     * @param votingEndTimestamp  the timestamp when voting ends
     * @param totalVoteCount      the total number of votes
     * @param voteUpdatesRemained the number of vote updates remaining
     * @param isPrediction        indicates if this is a prediction poll
     * @param resolvedOptionId    the ID of the resolved option
     * @param userWonAmount       the amount won by the user
     * @param userSelection       the user's selection in the poll
     * @param options             the list of options in the poll
     * @param totalStakeAmount    the total stake amount in the poll
     */
    public record PollData(
            @JsonProperty("prediction_status") String predictionStatus,
            @JsonProperty("tournament_id") String tournamentId,
            @JsonProperty("voting_end_timestamp") Instant votingEndTimestamp,
            @JsonProperty("total_vote_count") Integer totalVoteCount,
            @JsonProperty("vote_updates_remained") Integer voteUpdatesRemained,
            @JsonProperty("is_prediction") Boolean isPrediction,
            @JsonProperty("resolved_option_id") String resolvedOptionId,
            @JsonProperty("user_won_amount") Double userWonAmount,
            @JsonProperty("user_selection") String userSelection,
            @JsonProperty("options") List<Option> options,
            @JsonProperty("total_stake_amount") Double totalStakeAmount
    ) {
        /**
         * Represents an option in a Reddit poll.
         *
         * @param text the text of the option
         * @param id   the unique identifier of the option
         */
        public record Option(@JsonProperty("text") String text, @JsonProperty("id") String id) {}
    }

    /**
     * Represents the media associated with a Reddit post. This can include video content, images, or other media
     * types.
     *
     * @param redditVideo the Reddit video associated with the post, if any
     */
    public record Media(@JsonProperty("reddit_video") RedditVideo redditVideo) {}

    /**
     * Represents a Reddit video with various attributes.
     *
     * @param bitrateKbps       the bitrate of the video in kilobits per second
     * @param fallbackUrl       the fallback URL for the video
     * @param hasAudio          indicates if the video has audio
     * @param height            the height of the video in pixels
     * @param width             the width of the video in pixels
     * @param scrubberMediaUrl  the URL for the scrubber media
     * @param dashUrl           the DASH URL for adaptive streaming
     * @param duration          the duration of the video
     * @param hlsUrl            the HLS URL for streaming
     * @param isGif             indicates if the video is a GIF
     * @param transcodingStatus the status of transcoding for the video
     */
    @Builder
    public record RedditVideo(
            @JsonProperty("bitrate_kbps") Integer bitrateKbps,
            @JsonProperty("fallback_url") URL fallbackUrl,
            @JsonProperty("has_audio") Boolean hasAudio,
            @JsonProperty("height") Integer height,
            @JsonProperty("width") Integer width,
            @JsonProperty("scrubber_media_url") URL scrubberMediaUrl,
            @JsonProperty("dash_url") URL dashUrl,
            @JsonProperty("duration") Duration duration,
            @JsonProperty("hls_url") URL hlsUrl,
            @JsonProperty("is_gif") Boolean isGif,
            @JsonProperty("transcoding_status") TranscodingStatus transcodingStatus
    ) {
        /**
         * Represents the status of transcoding for a Reddit video. This can indicate whether the video has been
         * successfully transcoded.
         */
        public enum TranscodingStatus {
            /**
             * Indicates that the transcoding is in progress.
             */
            @JsonProperty("completed") COMPLETED
        }
    }

    /**
     * Represents rich text formatting for flairs in Reddit posts.
     *
     * @param emoji the emoji associated with the flair
     * @param url   the URL associated with the flair, if any
     * @param type  the type of the flair (e.g., emoji, text)
     */
    public record FlairRichtext(
            @JsonProperty("a") String emoji,
            @JsonProperty("u") URL url,
            @JsonProperty("t") String type
    ) {
    }

    /**
     * Represents the media embed information for a Reddit post. This can include content, height, scrolling behavior,
     * and width of the embedded media.
     *
     * @param content   the content of the media embed
     * @param height    the height of the media embed
     * @param scrolling indicates if scrolling is enabled for the media embed
     * @param width     the width of the media embed
     */
    public record MediaEmbed(
            @JsonProperty("content") String content,
            @JsonProperty("height") Integer height,
            @JsonProperty("scrolling") Boolean scrolling,
            @JsonProperty("width") Integer width
    ) {}

    /**
     * Represents an awarding given to a Reddit post.
     *
     * @param giverCoinReward                  the coin reward given by the user who awarded
     * @param subredditId                      the ID of the subreddit where the award was given
     * @param isNew                            indicates if this is a new award
     * @param daysOfDripExtension              the number of days of drip extension for the award
     * @param coinPrice                        the price of the award in coins
     * @param id                               the unique identifier of the award
     * @param pennyDonate                      the amount donated in pennies
     * @param coinReward                       the coin reward for the award
     * @param iconUrl                          the URL of the icon representing the award
     * @param daysOfPremium                    the number of days of premium granted by the award
     * @param iconHeight                       the height of the icon in pixels
     * @param tiersByRequiredAwardings         tiers by required awardings, if applicable
     * @param iconWidth                        the width of the icon in pixels
     * @param staticIconWidth                  the static width of the icon in pixels
     * @param startDate                        when this award starts being valid
     * @param isEnabled                        indicates if this award is enabled
     * @param awardingsRequiredToGrantBenefits number of awards required to grant benefits
     * @param description                      a description of what this award does or represents
     * @param endDate                          when this award stops being valid, if applicable
     * @param subredditCoinReward              subreddit coin reward, if applicable
     * @param count                            how many times this award has been given out
     * @param staticIconHeight                 static height of the icon in pixels
     * @param name                             name of the award
     * @param iconFormat                       format of the icon (e.g., png, jpg)
     * @param awardSubType                     sub-type of the award (e.g., global, community)
     * @param pennyPrice                       price in pennies, if applicable
     * @param awardType                        type of award (e.g., global, community)
     * @param staticIconUrl                    URL for a static version of the icon, if available
     */
    public record Awarding(
            @JsonProperty("giver_coin_reward") Integer giverCoinReward,
            @JsonProperty("subreddit_id") String subredditId,
            @JsonProperty("is_new") Boolean isNew,
            @JsonProperty("days_of_drip_extension") Integer daysOfDripExtension,
            @JsonProperty("coin_price") Integer coinPrice,
            @JsonProperty("id") String id,
            @JsonProperty("penny_donate") Integer pennyDonate,
            @JsonProperty("coin_reward") Integer coinReward,
            @JsonProperty("icon_url") URL iconUrl,
            @JsonProperty("days_of_premium") Integer daysOfPremium,
            @JsonProperty("icon_height") Integer iconHeight,
            @JsonProperty("tiers_by_required_awardings") JsonNode tiersByRequiredAwardings,
            @JsonProperty("icon_width") Integer iconWidth,
            @JsonProperty("static_icon_width") Integer staticIconWidth,
            @JsonProperty("start_date") Instant startDate,
            @JsonProperty("is_enabled") Boolean isEnabled,
            @JsonProperty("awardings_required_to_grant_benefits") Integer awardingsRequiredToGrantBenefits,
            @JsonProperty("description") String description,
            @JsonProperty("end_date") Instant endDate,
            @JsonProperty("subreddit_coin_reward") Integer subredditCoinReward,
            @JsonProperty("count") Integer count,
            @JsonProperty("static_icon_height") Integer staticIconHeight,
            @JsonProperty("name") String name,
            @JsonProperty("icon_format") String iconFormat,
            @JsonProperty("award_sub_type") String awardSubType,
            @JsonProperty("penny_price") Integer pennyPrice,
            @JsonProperty("award_type") String awardType,
            @JsonProperty("static_icon_url") URL staticIconUrl
    ) {}
}
