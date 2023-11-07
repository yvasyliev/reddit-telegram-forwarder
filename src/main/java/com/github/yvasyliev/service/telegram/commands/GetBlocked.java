package com.github.yvasyliev.service.telegram.commands;

import com.github.yvasyliev.service.repository.BlockedAuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.stream.Collectors;

@Service("/getblocked")
public class GetBlocked extends AdminCommand {
    @Autowired
    private BlockedAuthorRepository blockedAuthorRepository;

    @Override
    public void execute(Message message) throws Exception {
        var blockedAuthorTemplate = responseReader.applyWithException("responses/getblocked/blocked_author.md");
        var blockedAuthors = blockedAuthorRepository
                .findAll()
                .stream()
                .map(blockedAuthorTemplate::formatted)
                .collect(Collectors.joining("\n"));
        reply(message, "responses/getblocked/blocked_authors.md", blockedAuthors);
    }
}
