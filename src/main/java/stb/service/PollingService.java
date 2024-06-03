package stb.service;

import stb.bot.TGBot;
import stb.entity.Poll;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface PollingService {

    void pollingConstructor(Poll poll, TGBot bot) throws TelegramApiException;
    void proceedPolling(Update update, TGBot bot) throws TelegramApiException;
}
