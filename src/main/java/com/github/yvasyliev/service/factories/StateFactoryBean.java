package com.github.yvasyliev.service.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.service.json.State;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class StateFactoryBean implements FactoryBean<State> {
    @Autowired
    private File stateSrc;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public State getObject() throws IOException {
        return stateSrc.exists()
                ? objectMapper.readValue(stateSrc, State.class)
                : new State();
    }

    @Override
    public Class<?> getObjectType() {
        return State.class;
    }
}
