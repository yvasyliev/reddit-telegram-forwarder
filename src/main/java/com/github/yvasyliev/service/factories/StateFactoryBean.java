package com.github.yvasyliev.service.factories;

import com.github.yvasyliev.model.dto.State;
import com.github.yvasyliev.service.state.StateIO;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StateFactoryBean implements FactoryBean<State> {
    @Autowired
    private StateIO stateIO;

    @Override
    public State getObject() throws IOException {
        return stateIO.read();
    }

    @Override
    public Class<?> getObjectType() {
        return State.class;
    }
}
