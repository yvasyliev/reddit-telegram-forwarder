package com.github.yvasyliev.factories;

import com.github.yvasyliev.dto.RedditAccessToken;
import com.github.yvasyliev.service.reddit.api.Request;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

public class RedditAccessTokenFactory implements FactoryBean<RedditAccessToken> {
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
        return String.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
