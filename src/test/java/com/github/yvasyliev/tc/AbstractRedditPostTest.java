package com.github.yvasyliev.tc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.config.RedTelBotConfiguration;
import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.service.state.StateManager;
import com.github.yvasyliev.service.telegram.PostManager;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RedTelBotConfiguration.class)
abstract class AbstractRedditPostTest {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    StateManager stateManager;

    @Autowired
    PostManager postManager;

    Post parsePost(String path) throws IOException {
        var jsonNode = objectMapper.readTree(new ClassPathResource(path).getURL());
        return objectMapper.treeToValue(jsonNode.at("/data/children/0/data"), Post.class);
    }
}
