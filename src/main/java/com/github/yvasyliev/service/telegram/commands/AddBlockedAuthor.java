package com.github.yvasyliev.service.telegram.commands;

import com.github.yvasyliev.service.dao.BlockedAuthorService;
import com.github.yvasyliev.service.telegram.factory.UsernameParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service("/addblockedauthor")
public class AddBlockedAuthor extends Command {
    @Autowired
    protected BlockedAuthorService blockedAuthorService;
    @Autowired
    private UsernameParser usernameParser;

    @Override
    public void acceptWithException(Message message) throws Exception {
        var optionalUsername = usernameParser.apply(message);
        if (optionalUsername.isPresent()) {
            var username = optionalUsername.get();
            blockedAuthorService.block(username);
            reply(message, "responses/addblockedauthor.md", username, username);
        } else {
            reply(message, "responses/usernamenotrecognized.md");
        }
    }
}
