package com.github.yvasyliev.service.telegram.commands;

import com.github.yvasyliev.service.telegram.PostManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

@Service("/stop")
public class Stop extends AdminCommand {
    @Autowired
    private PostManager postManager;

    @Override
    public void execute(Message message) throws TelegramApiException, URISyntaxException, IOException {
        postManager.stopPublishing();
        redTelBot.stopPolling();
        reply(message, "responses/stop.md");
    }
}
