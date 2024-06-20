package org.stb.service;

import org.stb.bot.MyWebhookBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface ConnectionService {
    void handleConnectMessage(Update update, MyWebhookBot bot) throws TelegramApiException;

    void handleGetConnectForUser(Update update, MyWebhookBot bot) throws TelegramApiException;

    void handleSetConnectMessage(Update update, MyWebhookBot bot) throws TelegramApiException;

    void handleSetConnectForAdmin(Update update, MyWebhookBot bot) throws TelegramApiException;
}
