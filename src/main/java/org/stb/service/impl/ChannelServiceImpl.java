package org.stb.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stb.bot.TGBot;
import org.stb.entity.Channel;
import org.stb.repository.ChannelRepository;
import org.stb.service.ChannelService;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {
    private final ChannelRepository channelRepository;

    @Override
    public Channel getChannel(Update update, TGBot bot) throws TelegramApiException {
        Message message = update.getMessage();
        Channel channel = new Channel();
        if (!channelRepository.existsByChatId(message.getChatId())) {
//            channel.setChatId(message.getChatId());
            channel.setUsers(new HashSet<>());
            channel.setPost(new HashSet<>());

            GetChat getChat = new GetChat();
            getChat.setChatId(update.getMessage().getChatId());
            Chat chat = bot.execute(getChat);

//            WebStats webStats = new WebStats();

            getChat.setChatId(chat.getLinkedChatId());
            chat = bot.execute(getChat);

//            GetChat getChat = new GetChat();
//            getChat.setChatId(update.getMessage().getChatId());
//            Chat chat = bot.execute(getChat);

//            System.out.println(chat.getLinkedChatId());
//            getChat.setChatId(chat.getLinkedChatId());
//            chat = bot.execute(getChat);

            channel.setChannelId(chat.getId());
            channel.setTitle(chat.getTitle());
            channel.setChatId(chat.getLinkedChatId());

//            System.out.println(channel);

            channelRepository.save(channel);
        } else {
            channel = channelRepository.findFirstByChatId(message.getChatId());
        }
        return channel;
    }
}
