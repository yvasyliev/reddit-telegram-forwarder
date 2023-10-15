package com.github.yvasyliev.service.dao;

import com.github.yvasyliev.model.entity.UserCommand;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserCommandService extends AbstractService {
    public UserCommand setUserCommand(long userId, String command) {
        return sessionFactory.fromTransaction(session -> session.merge(new UserCommand(userId, command)));
    }

    public void removeUserCommand(long userId) {
        sessionFactory.inTransaction(session -> {
            var userCommand = session.find(UserCommand.class, userId);
            if (userCommand != null) {
                session.remove(userCommand);
            }
        });
    }

    public Optional<UserCommand> getUserCommand(long userId) {
        return sessionFactory.fromSession(session -> Optional.ofNullable(session.find(UserCommand.class, userId)));
    }
}
