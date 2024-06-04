package org.stb.service;

import org.stb.bot.TGBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface ConnectService {
    void handleConnectMessage(Update update, TGBot bot)  throws TelegramApiException ;

    void handleGetConnectForUser(Update update, TGBot bot) throws TelegramApiException ;

    void handleSetConnectMessage(Update update, TGBot bot) throws TelegramApiException ;

    void handleSetConnectForAdmin(Update update, TGBot bot) throws TelegramApiException ;
}
