package com.github.yvasyliev.service.dao;

import com.github.yvasyliev.model.entity.BlockedAuthor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockedAuthorService extends AbstractService {
    public BlockedAuthor get(String username) {
        return sessionFactory.fromSession(session -> session.find(BlockedAuthor.class, username));
    }

    public BlockedAuthor block(String username) {
        return sessionFactory.fromTransaction(session -> session.merge(new BlockedAuthor(username)));
    }

    public boolean isBlocked(String username) {
        return get(username) != null;
    }

    public void unblock(String username) {
        sessionFactory.inTransaction(session -> {
            var blockedAuthor = session.find(BlockedAuthor.class, username);
            if (blockedAuthor != null) {
                session.remove(blockedAuthor);
            }
        });
    }

    public List<BlockedAuthor> findAll() {
        return sessionFactory.fromSession(session -> session.createQuery("from BlockedAuthor", BlockedAuthor.class).list());
    }
}
