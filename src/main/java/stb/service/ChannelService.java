package stb.service;

import stb.bot.TGBot;
import stb.entity.Channel;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface ChannelService {
    Channel getChannel(Update update, TGBot bot) throws TelegramApiException;
}
