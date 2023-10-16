package com.github.yvasyliev.service.telegram.commands;

import com.github.yvasyliev.service.state.StateManager;
import com.github.yvasyliev.service.telegram.factory.UsernameParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

@Service("/addblockedauthor")
public class AddBlockedAuthor extends Command {
    @Autowired
    private UsernameParser usernameParser;

    @Autowired
    private StateManager stateManager;

    @Override
    public void acceptWithException(Message message) throws IOException, TelegramApiException, URISyntaxException {
        var optionalUsername = usernameParser.apply(message);
        if (optionalUsername.isPresent()) {
            var username = optionalUsername.get();
            stateManager.addBlockedAuthor(username);
            reply(message, "responses/addblockedauthor.md", username, username);
        } else {
            reply(message, "responses/usernamenotrecognized.md");
        }
    }
}
