package com.github.yvasyliev.service.telegram.commands;

import com.github.yvasyliev.service.telegram.ScheduledPostManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

// TODO: 11/8/2023 rename to pauseposting
@Service("/pausepublishing")
public class PausePublishing extends AdminCommand {
    @Autowired
    private ScheduledPostManager postManager;

    @Override
    protected void execute(Message message) throws TelegramApiException, URISyntaxException, IOException {
        postManager.pausePosting();
        redditTelegramForwarderBot.execute(new SendMessage(
                message.getChatId().toString(),
                responseReader.applyWithException("responses/pausepublishing.md")
        ));
    }
}
