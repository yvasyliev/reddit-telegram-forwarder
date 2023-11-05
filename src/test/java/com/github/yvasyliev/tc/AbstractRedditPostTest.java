package com.github.yvasyliev.tc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yvasyliev.model.dto.post.Post;
import com.github.yvasyliev.service.state.StateManager;
import com.github.yvasyliev.service.telegram.PostManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@SpringBootTest
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
