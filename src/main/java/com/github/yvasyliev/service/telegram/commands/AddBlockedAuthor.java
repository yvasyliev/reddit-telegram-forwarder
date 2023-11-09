package com.github.yvasyliev.service.telegram.commands;

import com.github.yvasyliev.service.data.BlockedAuthorService;
import com.github.yvasyliev.service.telegram.MarkdownV2Escaper;
import com.github.yvasyliev.service.telegram.factory.UsernameParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

@Service("/addblockedauthor")
public class AddBlockedAuthor extends AdminCommand {
    @Autowired
    private UsernameParser usernameParser;

    @Autowired
    private BlockedAuthorService blockedAuthorService;

    @Autowired
    private MarkdownV2Escaper markdownV2Escaper;

    @Override
    protected void execute(Message message) throws IOException, TelegramApiException, URISyntaxException {
        var optionalUsername = usernameParser.apply(message);
        if (optionalUsername.isPresent()) {
            var username = optionalUsername.get();
            blockedAuthorService.saveBlockedAuthor(username);
            username = markdownV2Escaper.apply(username);
            reply(message, "responses/addblockedauthor.md", username, username);
        } else {
            reply(message, "responses/usernamenotrecognized.md");
        }
    }
}
