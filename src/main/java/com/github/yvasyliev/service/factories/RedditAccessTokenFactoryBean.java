package com.github.yvasyliev.service.factories;

import com.github.yvasyliev.model.dto.RedditAccessToken;
import com.github.yvasyliev.service.reddit.api.Request;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedditAccessTokenFactoryBean implements FactoryBean<RedditAccessToken> {
    @Autowired
    private Request<RedditAccessToken> getRedditAccessToken;

    private RedditAccessToken redditAccessToken;

    @Override
    public RedditAccessToken getObject() throws Exception {
        if (redditAccessToken == null || redditAccessToken.isExpired()) {
            redditAccessToken = getRedditAccessToken.execute();
        }
        return redditAccessToken;
    }

    @Override
    public Class<?> getObjectType() {
        return RedditAccessToken.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
