package stb.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stb.bot.TGBot;
import stb.entity.Channel;
import stb.repository.ChannelRepository;
import stb.service.ConnectionService;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import static stb.util.Util.*;

@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionService.class);
    private static final Map<UUID, Long> TEMP_MAP = new HashMap<>();
    private final ChannelRepository channelRepository;

    @Override
    public void handleConnectMessage(Update update, TGBot bot) throws TelegramApiException {
        Message message = update.getMessage();
        if (message.getChat().isUserChat()) {
            handleGetConnectForUser(update, bot);
        } else if (isUserAdmin(message.getChatId(), message.getFrom().getId(), bot)) {
            delete(update, bot);
            handleSetConnectMessage(update, bot);
        } else {
            delete(update, bot);
        }
    }

    @Override
    public void handleGetConnectForUser(Update update, TGBot bot) throws TelegramApiException {
        Message message = update.getMessage();
        long chatId = message.getChatId();
        UUID id = UUID.randomUUID();
        TEMP_MAP.put(id, chatId);
        bot.execute(toMessage(chatId, id.toString()));
    }

    @Override
    public void handleSetConnectMessage(Update update, TGBot bot) throws TelegramApiException {
        LOGGER.info("З`єднання адміна з каналом");
        handleSetConnectForAdmin(update, bot);
    }

    @Override
    @Transactional
    public void handleSetConnectForAdmin(Update update, TGBot bot) throws TelegramApiException {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String[] ss = message.getText().split(" ");
        if (ss.length != 2) {
            Message re = bot.execute(toMessage(message.getChat().getId(), "Очікується реєстраційний код"));
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            delete(re.getChatId(), re.getMessageId(), bot);
            return;
        }
//        LOGGER.info(String.valueOf(update));





        GetChat getChat = new GetChat();
        getChat.setChatId(update.getMessage().getChatId());
        Chat mainChannel = bot.execute(getChat);
        LOGGER.info("mainChannel: {}", mainChannel); // <----------------- HERE
        UUID uid = UUID.fromString(ss[1]);
        if (TEMP_MAP.containsKey(uid)) {
            Long userId = TEMP_MAP.get(uid);
            if (!channelRepository.existsByChatId(update.getMessage().getChatId())) {
                Channel channel = new Channel();
                channel.setChatId(update.getMessage().getChatId());
                channel.setChannelId(mainChannel.getId());
                channel.setAdmins(new HashSet<>());
                channel.setTitle(mainChannel.getTitle());

                channelRepository.save(channel);
            }
            Channel channel = channelRepository.findFirstByChatId(update.getMessage().getChatId());

            if (!channel.containsAdmin(userId)) {
                channel.addAdmin(userId);

            } else {
                bot.execute(toMessage(userId, "Ви вже зареєстровані як адмін в цьому каналі"));
                TEMP_MAP.remove(uid);
                return;
            }
            TEMP_MAP.remove(uid);
            GetChatMember getChatMember = new GetChatMember();
            getChatMember.setChatId(chatId);
            getChatMember.setUserId(userId);
            ChatMember chatMember = bot.execute(getChatMember);
            bot.execute(toMessage(userId, "Успішне з`єднання"));
            LOGGER.info("З`єднано {} -> {}", chatMember.getUser().getUserName(), update.getMessage().getChat().getTitle());
            return;
        }
        bot.execute(toMessage(chatId, "Я незнаю такого коду"));
    }
}
