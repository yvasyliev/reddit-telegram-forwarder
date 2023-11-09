package com.github.yvasyliev.service.telegram.commands;

import com.github.yvasyliev.service.data.BlockedAuthorService;
import com.github.yvasyliev.service.telegram.MarkdownV2Escaper;
import com.github.yvasyliev.service.telegram.factory.UsernameParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service("/removeblockedauthor")
public class RemoveBlockedAuthor extends AdminCommand {

    @Autowired
    private UsernameParser usernameParser;

    @Autowired
    private MarkdownV2Escaper markdownV2Escaper;

    @Autowired
    private BlockedAuthorService blockedAuthorService;

    @Override
    protected void execute(Message message) throws Exception {
        var optionalUsername = usernameParser.apply(message);
        if (optionalUsername.isPresent()) {
            var username = optionalUsername.get();
            blockedAuthorService.removeBlockedAuthor(username);
            reply(message, "responses/removeblockedauthor.md", markdownV2Escaper.apply(username));
        } else {
            reply(message, "responses/usernamenotrecognized.md");
        }
    }
}
