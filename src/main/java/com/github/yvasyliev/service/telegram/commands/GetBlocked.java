package com.github.yvasyliev.service.telegram.commands;

import com.github.yvasyliev.service.dao.BlockedAuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.stream.Collectors;

@Service("/getblocked")
public class GetBlocked extends Command {
    @Autowired
    protected BlockedAuthorService blockedAuthorService;

    @Override
    public void acceptWithException(Message message) throws Exception {
        var blockedAuthors = blockedAuthorService
                .findAll()
                .stream()
                .map(blockedAuthor -> "👤 " + blockedAuthor.getUsername())
                .collect(Collectors.joining("\n"));
        reply(message, "responses/getblocked.md", blockedAuthors);
    }
}
