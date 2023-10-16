package com.github.yvasyliev.service.telegram.commands;

import com.github.yvasyliev.service.state.StateManager;
import com.github.yvasyliev.service.telegram.factory.UsernameParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service("/removeblockedauthor")
public class RemoveBlockedAuthor extends Command {

    @Autowired
    private UsernameParser usernameParser;

    @Autowired
    private StateManager stateManager;

    @Override
    public void acceptWithException(Message message) throws Exception {
        var optionalUsername = usernameParser.apply(message);
        if (optionalUsername.isPresent()) {
            var username = optionalUsername.get();
            stateManager.removeBlockedAuthor(username);
            reply(message, "responses/removeblockedauthor.md", username);
        } else {
            reply(message, "responses/usernamenotrecognized.md");
        }
    }
}
