package com.github.yvasyliev.service.telegram;

import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.service.data.RedditTelegramForwarderPropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingSupplier;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ScheduledPostManager extends PostManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledPostManager.class);

    @Value("#{new java.util.concurrent.atomic.AtomicBoolean(${telegram.schedule.posting.enabled:true})}")
    private AtomicBoolean isPosting;

    @Autowired
    private ThrowingSupplier<List<Post>> subredditNewSupplier;

    @Autowired
    private RedditTelegramForwarderPropertyService propertyService;


    @Scheduled(fixedDelayString = "${telegram.schedule.posting.delay.in.minutes:1}", timeUnit = TimeUnit.MINUTES)
    public void shareNewPosts() {
        if (isPosting.get()) {
            try {
                var lastCreated = propertyService.findLastCreated().orElse(0);
                LOGGER.debug("lastCreated={}", lastCreated);
                var newPosts = subredditNewSupplier.getWithException();
                LOGGER.debug("New posts: {}", newPosts);
                publishPosts(newPosts);
            } catch (Exception e) {
                LOGGER.error("Failed to find new posts.", e);
            }
        }
    }

    public void pausePosting() {
        isPosting.set(false);
    }

    public void resumePosting() {
        isPosting.set(true);
    }
}
