package com.github.yvasyliev.service.telegram.commands;

import com.github.yvasyliev.service.state.StateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.stream.Collectors;

@Service("/getblocked")
public class GetBlocked extends AdminCommand {
    @Autowired
    private StateManager stateManager;

    @Override
    public void execute(Message message) throws Exception {
        var blockedAuthorTemplate = responseReader.applyWithException("responses/getblocked/blocked_author.md");
        var blockedAuthors = stateManager
                .getBlockedAuthors()
                .stream()
                .map(blockedAuthorTemplate::formatted)
                .collect(Collectors.joining("\n"));
        reply(message, "responses/getblocked/blocked_authors.md", blockedAuthors);
    }
}
