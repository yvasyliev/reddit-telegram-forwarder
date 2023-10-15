package com.github.yvasyliev.service.dao;

import com.github.yvasyliev.model.entity.State;
import org.springframework.stereotype.Service;

@Service
public class StateService extends AbstractService {
    public int getLastCreated() {
        return sessionFactory.fromSession(session -> {
            var lastCreated = session.get(State.class, "last_created");
            return lastCreated != null
                    ? Integer.parseInt(lastCreated.getValue())
                    : 0;
        });
    }

    public State setLastCreated(int created) {
        return sessionFactory.fromTransaction(session -> session.merge(new State("last_created", String.valueOf(created))));
    }
}
