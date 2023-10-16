package com.github.yvasyliev.service.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.model.dto.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class StateIO {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private File stateSrc;

    public State read() throws IOException {
        return stateSrc.exists()
                ? objectMapper.readValue(stateSrc, State.class)
                : new State();
    }

    public void write(State state) throws IOException {
        objectMapper.writeValue(stateSrc, state);
    }
}
