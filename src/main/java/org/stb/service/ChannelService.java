package org.stb.service;

import org.stb.bot.TGBot;
import org.stb.entity.Channel;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface ChannelService {
    Channel getChannel(Update update, TGBot bot) throws TelegramApiException;
}
