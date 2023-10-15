package com.github.yvasyliev.service.telegram.commands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.URISyntaxException;

@Service("/resumepublishing")
public class ResumePublishing extends Command {
    @Override
    public void acceptWithException(Message message) throws TelegramApiException, URISyntaxException, IOException {
        redTelBot.resumePublishing();
        redTelBot.execute(new SendMessage(
                message.getChatId().toString(),
                responseReader.applyWithException("responses/resumepublishing.md")
        ));
    }
}
