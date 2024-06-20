package org.stb.service;

import org.stb.bot.MyWebhookBot;
import org.stb.entity.Channel;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.UUID;

public interface CallBackService {
    void proceedCallBack(CallbackQuery callbackQuery, MyWebhookBot bot) throws TelegramApiException;

    SendMessage createCallBack(Update update) throws TelegramApiException;

    SendMessage createButtons(List<Channel> channels, UUID chatId, Update update);
}
