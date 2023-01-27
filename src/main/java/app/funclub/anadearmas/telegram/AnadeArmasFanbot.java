package app.funclub.anadearmas.telegram;

import app.funclub.anadearmas.exceptions.UnhandledDataFormatException;
import com.github.masecla.RedditClient;
import com.github.masecla.objects.reddit.Item;
import com.github.masecla.objects.reddit.Link;
import com.github.masecla.objects.reddit.Metadata;
import com.github.masecla.objects.reddit.Resolution;
import com.github.masecla.objects.reddit.Thing;
import com.github.masecla.objects.response.GetSubredditNewResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AnadeArmasFanbot extends TelegramLongPollingBot {
    @Autowired
    private RedditClient redditClient;

    @Autowired
    private Properties appProperties;

    private String channelId;

    private String developerId;

    @PostConstruct
    public void initialize() {
        channelId = appProperties.getProperty("channelId");
        developerId = appProperties.getProperty("developerId");
    }

    public void processRedditPosts() {
        try {
            GetSubredditNewResponse subredditNew = redditClient.getSubredditNew("AnadeArmas").rawJson().execute();
            List<Thing<Link>> children = subredditNew.getData().getChildren();
            Collections.reverse(children);

            long created = Long.parseLong(appProperties.getProperty("created", "0"));

            for (Thing<Link> child : children) {
                Link link = child.getData();
                if (link.getCreated() > created && !link.isHidden()) {
                    processRedditPost(link);

                    created = link.getCreated();
                    appProperties.setProperty("created", String.valueOf(created));
                    TimeUnit.SECONDS.sleep(10);
                }
            }
        } catch (TelegramApiException | IOException | UnhandledDataFormatException | InterruptedException e) {
            String message = e.getMessage();
            try {
                if (!(e instanceof UnhandledDataFormatException)) {
                    try (StringWriter stringWriter = new StringWriter(); PrintWriter printWriter = new PrintWriter(stringWriter)) {
                        e.printStackTrace(printWriter);
                        message = stringWriter.toString();
                    }
                }
                sendDeveloperMessage(message);
            } catch (TelegramApiException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void processRedditPost(Link link) throws TelegramApiException, IOException, UnhandledDataFormatException, InterruptedException {
        if (isGif(link)) {
            String gifUrl = link.getPreview()
                    .getImages()
                    .get(0)
                    .getVariants()
                    .getMp4()
                    .getSource()
                    .getUrl();

            sendGif(gifUrl, link.getTitle(), "nsfw".equals(link.getThumbnail()));
        } else if (link.getGalleryData() != null) {
            List<String> photoUrls = link.getGalleryData()
                    .getItems()
                    .stream()
                    .map(Item::getMediaId)
                    .map(mediaId -> link.getMediaMetadata().get(mediaId))
                    .map(Metadata::getP)
                    .map(Collection::stream)
                    .map(stream -> stream.max(Comparator.comparingInt(Resolution::getWidth)))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(Resolution::getUrl)
                    .collect(Collectors.toList());

            boolean manyPhotos = photoUrls.size() > 10;
            List<String> photoUrlsPage = manyPhotos ? photoUrls.subList(0, 10) : photoUrls;

            sendMultiplePhotos(photoUrlsPage, link.getTitle(), "nsfw".equals(link.getThumbnail()));

            if (manyPhotos) {
                photoUrls.removeAll(photoUrlsPage);
                do {
                    TimeUnit.SECONDS.sleep(10);
                    int size = photoUrls.size();
                    int toIndex = Math.min(size, 10);
                    photoUrlsPage = photoUrls.subList(0, toIndex);
                    sendMultiplePhotos(photoUrlsPage, null, "nsfw".equals(link.getThumbnail()));
                    photoUrls.removeAll(photoUrlsPage);
                } while (!photoUrls.isEmpty());
            }
        } else if (link.getMedia() != null && link.getMedia().getRedditVideo() != null) {
            String videoUrl = link.getMedia().getRedditVideo().getFallbackUrl();
            sendVideo(videoUrl, link.getTitle(), "nsfw".equals(link.getThumbnail()));
        } else if (link.getPreview() != null && link.getPreview().getRedditVideoPreview() != null && !"t2_dy21ymq9".equals(link.getAuthorFullname())) {
            String videoUrl = link.getPreview().getRedditVideoPreview().getFallbackUrl();
            sendVideo(videoUrl, link.getTitle(), "nsfw".equals(link.getThumbnail()));
        } else if ("youtube.com".equals(link.getDomain()) || "youtu.be".equals(link.getDomain())) {
            String text = link.getTitle() + "\n\n" + link.getUrlOverriddenByDest();
            sendText(text);
        } else if (link.getUrlOverriddenByDest().endsWith(".jpg1")) {
            String photoUrl = link.getUrlOverriddenByDest().substring(0, link.getUrlOverriddenByDest().length() - 1);
            sendPhoto(photoUrl, link.getTitle(), "nsfw".equals(link.getThumbnail()));
        } else if (link.getUrlOverriddenByDest().endsWith(".jpg") || link.getUrlOverriddenByDest().endsWith(".png") || link.getUrlOverriddenByDest().endsWith("jpeg")) {
            String photoUrl = link.getPreview()
                    .getImages()
                    .get(0)
                    .getSource()
                    .getUrl();

            photoUrl = photoUrl.contains("auto=webp") ? link.getUrlOverriddenByDest() : photoUrl;

            try {
                sendPhoto(photoUrl, link.getTitle(), "nsfw".equals(link.getThumbnail()));
            } catch (TelegramApiRequestException e) {
                if (!e.getApiResponse().contains("PHOTO_INVALID_DIMENSIONS") && !e.getApiResponse().endsWith("too big for a photo")) {
                    throw e;
                }
                sendDocument(photoUrl, link.getTitle());
            }
        } else if (link.getCrosspostParentList() != null && !link.getCrosspostParentList().isEmpty()) {
            processRedditPost(link.getCrosspostParentList().get(0));
        } else if ("link".equals(link.getPostHint())) {
            sendText(link.getTitle() + "\n" + link.getUrlOverriddenByDest());
        } else {
            throw new UnhandledDataFormatException("Could not handle post. Created: " + link.getCreated() + ", URL: " + link.getUrlOverriddenByDest());
        }
    }

    private boolean isGif(Link link) {
        return link.getUrlOverriddenByDest().endsWith(".gif")
                && link.getPreview().getImages().get(0).getVariants().getMp4() != null;
    }

    private void sendGif(String gifUrl, String text, boolean hasSpoiler) throws TelegramApiException, IOException {
        try (InputStream inputStream = new URL(gifUrl).openStream()) {
            String fileName = gifUrl.substring(gifUrl.lastIndexOf('/') + 1);
            fileName = fileName.contains("?") ? fileName.substring(0, fileName.indexOf("?")) : fileName;
            SendAnimation sendAnimation = SendAnimation.builder()
                    .chatId(channelId)
                    .animation(new InputFile(inputStream, fileName))
                    .caption(text)
                    .hasSpoiler(hasSpoiler)
                    .build();

            execute(sendAnimation);
        }
    }

    private void sendMultiplePhotos(List<String> photoUrls, String text, boolean hasSpoiler) throws TelegramApiException {
        List<InputMedia> inputMedia = photoUrls.stream()
                .map(InputMediaPhoto::new)
                .peek(inputMediaPhoto -> inputMediaPhoto.setHasSpoiler(hasSpoiler))
                .collect(Collectors.toList());

        inputMedia.get(0).setCaption(text);

        SendMediaGroup sendMediaGroup = SendMediaGroup.builder()
                .chatId(channelId)
                .medias(inputMedia)
                .build();

        execute(sendMediaGroup);
    }

    private void sendVideo(String videoUrl, String text, boolean hasSpoiler) throws TelegramApiException, IOException {
        try (InputStream inputStream = new URL(videoUrl).openStream()) {
            String fileName = videoUrl.substring(videoUrl.lastIndexOf('/') + 1);
            SendVideo sendVideo = SendVideo.builder()
                    .chatId(channelId)
                    .video(new InputFile(inputStream, fileName))
                    .caption(text)
                    .hasSpoiler(hasSpoiler)
                    .build();

            execute(sendVideo);
        }
    }

    private void sendText(String text) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage(channelId, text);
        execute(sendMessage);
    }

    private void sendPhoto(String photoUrl, String text, boolean hasSpoiler) throws TelegramApiException, IOException {
        try (InputStream inputStream = new URL(photoUrl).openStream()) {
            String fileName = photoUrl.substring(photoUrl.lastIndexOf('/') + 1);
            SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(channelId)
                    .photo(new InputFile(inputStream, fileName))
                    .caption(text)
                    .hasSpoiler(hasSpoiler)
                    .build();

            execute(sendPhoto);
        }
    }

    private void sendDocument(String docUrl, String text) throws TelegramApiException, IOException {
        try (InputStream inputStream = new URL(docUrl).openStream()) {
            String fileName = docUrl.substring(docUrl.lastIndexOf('/') + 1);
            SendDocument sendDocument = SendDocument.builder()
                    .chatId(channelId)
                    .document(new InputFile(inputStream, fileName))
                    .caption(text)
                    .build();

            execute(sendDocument);
        }
    }

    private void sendDeveloperMessage(String text) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(developerId)
                .text("```" + text + "```")
                .parseMode(ParseMode.MARKDOWN)
                .build();
        execute(sendMessage);
    }

    @Override
    public String getBotUsername() {
        return "Ana_de_Armas_fanbot";
    }

    @Override
    public String getBotToken() {
        return "5447965600:AAEqVNRqOixVdxORFWQgaSywzCfRo6-tBus";
    }

    @Override
    public void onUpdateReceived(Update update) {
    }
}
