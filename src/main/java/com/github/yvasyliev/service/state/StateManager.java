package com.github.yvasyliev.service.state;

import com.github.yvasyliev.model.dto.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;

@Service
public class StateManager {
    @Autowired
    private State state;

    @Autowired
    private StateIO stateIO;

    public int getLastCreated() {
        return state.getLastCreated();
    }

    public Set<String> getBlockedAuthors() {
        return Set.copyOf(state.getBlockedAuthors());
    }

    public void setLastCreated(int lastCreated) throws IOException {
        state.setLastCreated(lastCreated);
        save();
    }

    public void addBlockedAuthor(String username) throws IOException {
        state.getBlockedAuthors().add(username);
        save();
    }

    public void removeBlockedAuthor(String username) throws IOException {
        state.getBlockedAuthors().remove(username);
        save();
    }

    public void save() throws IOException {
        stateIO.write(state);
    }
}
