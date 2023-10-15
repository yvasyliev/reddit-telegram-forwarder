package com.github.yvasyliev.service.dao;

import com.github.yvasyliev.model.entity.Post;
import org.springframework.stereotype.Service;

@Service
public class PostService extends AbstractService {
    public Post save(Post post) {
        return sessionFactory.fromTransaction(session -> session.merge(post));
    }

    public Post get(int created) {
        return sessionFactory.fromTransaction(session -> session.get(Post.class, created));
    }

    public void remove(Post post) {
        sessionFactory.inTransaction(session -> session.remove(post));
    }
}
