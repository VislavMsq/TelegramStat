package org.stb.bot;

import lombok.RequiredArgsConstructor;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.example.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stb.entity.*;
import org.stb.entity.enums.Status;
import org.stb.repository.*;
import org.stb.service.*;
import org.stb.util.SpecialOption;
import org.stb.util.Util;
import org.telegram.telegrambots.meta.api.methods.groupadministration.ExportChatInviteLink;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatInviteLink;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class BotMethods {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotMethods.class);
    private final ChannelService channelService;
    private final PostService postService;
    private final UserService userService;
    private final MessageService messageService;
    private final PollingService pollingService;
    private final OTPRepository otpRepository;
    private final ButtonRawRepository buttonRawRepository;
    private final PollRawRepository pollRawRepository;
    private final PollRepository pollRepository;
    private final CallBackService callBackService;
    private final ConnectionService connectionService;
    private final WebStatsRepository webStatsRepository;
    private final PostRepository postRepository;

    @Transactional
    public void handleUpdate(Update update, MyWebhookBot bot) throws TelegramApiException, ExecutionException, InterruptedException {
        if (update.hasCallbackQuery()) {
            callBackService.proceedCallBack(update.getCallbackQuery(), bot);
            return;
        }
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.getFrom().getFirstName().equals("Telegram")) {

                String text = null;
                String fileUniqueId = null;

                if (message.hasText()) {
                    text = message.getText();
                } else if (message.getCaption() != null) {
                    text = message.getCaption();
//                    fileUniqueId = message.getVideoNote().getFileUniqueId();
                }

                GetChat getChat = new GetChat();
                getChat.setChatId(update.getMessage().getChatId());
                Chat chat = bot.execute(getChat);

                WebStats webStats = new WebStats();

                getChat.setChatId(chat.getLinkedChatId());
                chat = bot.execute(getChat);

                SpecialOption<List<TdApi.Message>> optionMessageList = Example.getChatHistory(chat.getId(), 10).join();
                if (optionMessageList.getStatus() == Status.EXCEPTION) {
                    ExportChatInviteLink exportChatInviteLink = new ExportChatInviteLink();
                    exportChatInviteLink.setChatId(chat.getId());
                    String inviteLink;
                    try {
                        inviteLink = bot.execute(exportChatInviteLink);
                    } catch (TelegramApiException e) {
                        bot.execute(Util.toMessage(chat.getId(), "У бота недостаточно прав для создания ссылки-приглашения"));
                        LOGGER.error("Ошибка при создании ссылки-приглашения: {}", e.getMessage());
                        return;
                    }
                    boolean b = Example.joinChatByInviteLink(inviteLink);
                    if (!b) {
                        GetChatAdministrators getChatAdministrators = new GetChatAdministrators();
                        getChatAdministrators.setChatId(chat.getId());
                        ArrayList<ChatMember> chatAdministrators = bot.execute(getChatAdministrators);
                        Optional<ChatMember> creator = chatAdministrators.stream()
                                .filter(chatMember -> chatMember.getStatus().equals("creator"))
                                .findFirst();
                        if (creator.isPresent()) {
                            ChatMember chatMember = creator.get();
                            chatMember.getUser().getId();
                            bot.execute(Util.toMessage(chatMember.getUser().getId(), "Ошибка с ботом"));
                            LOGGER.error("ошибка во время подключения компонента Бота к каналу: {}", chat.getTitle());

                        }
                    }
                }

                TdApi.Message msg = null;
                SpecialOption<TdApi.Message> specialOption = null;
                int delayMinutes = 5;

                for (int i = 0; i < 5; i++) {
                    specialOption = Example.getMessageWithText(chat.getId(), 10, text).join();
                    if (specialOption.getStatus() == Status.OK) {
                        msg = specialOption.get();
                        break;
                    } else if (specialOption.getStatus() == Status.EMPTY) {
                        LOGGER.warn("message not found: {}", text);
                        LOGGER.info("retry in {} minutes", delayMinutes);
                    } else if (specialOption.getStatus() == Status.NULL) {
                        LOGGER.error("message has not content");
                        return;
                    } else {
                        LOGGER.info("error while getting message");
                        return;
                    }
                    Thread.sleep(delayMinutes * 60000);
                    delayMinutes *= 2; // double the delay for the next iteration
                }

                if (specialOption.getStatus() == Status.EMPTY || msg == null) {
                    LOGGER.warn("message not found: {}", text);
                    return;
                } else if (specialOption.getStatus() == Status.NULL) {
                    LOGGER.error("message has not content");
                    return;
                } else if (specialOption.getStatus() != Status.OK) {
                    LOGGER.info("error while getting message");
                    return;
                }

                webStats.setChannelId(chat.getId());
                webStats.setGlobalId(msg.id);
                webStats.setLocalId(update.getMessage().getMessageId());
                webStats.setViewCount(0);

                webStats.setReactionCount(0);
                webStats.setReplyCount(0);

                LocalDateTime now = LocalDateTime.now();

                webStats.setLastUpdateReaction(now);
                webStats.setLastUpdateReply(now);
                webStats.setLastUpdateView(now);

                webStatsRepository.save(webStats);
                LOGGER.info("webStats created: {}", webStats);
//                System.out.println(webStats);

                Post post = new Post();
                post.setTelegramId(update.getMessage().getMessageId());
                post.setChannel(channelService.getChannel(update, bot));
                post.setPostTime(LocalDateTime.now());

                postRepository.save(post);

            }
        }

        if (!update.hasMessage()) {
            return;
        }
        if (!update.getMessage().hasText()) {
            // вернемся, отвечаю
            return;
        }
        if (!update.getMessage().getChat().isUserChat()) {
            if (!Util.isUserAdmin(update.getMessage().getChatId(), update.getMessage().getFrom().getId(), bot)) {
                checkMessage(update, bot);
                if (update.getMessage().getText().startsWith("/")) {
                    Util.delete(update, bot);
                }
                return;
            }
        }
        if (update.getMessage().getText().startsWith("/connect")) {
            connectionService.handleConnectMessage(update, bot);
            return;
        }
        if (update.getMessage().getText().startsWith("/start")) {
            handleStartMessage(update, bot);
            return;
        }

//        if (update.getMessage().getText().startsWith("/test")) {
//            bot.execute(toMessage(update.getMessage().getChatId(), update.getMessage().getMessageId().toString()));
//        }

        if (update.getMessage().getText().startsWith("/authorize")) {
            proceedUserMessage(update, bot);
            return;
        }
        if (update.getMessage().getText().startsWith("/poll")) {
            bot.execute(callBackService.createCallBack(update));
        }
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            proceedAdminMessage(update, bot);
//        }
    }

    // має перетворити рав в нормальнне ентіті та додати деякі фішки типу зробити колбек дата
    private void handleStartMessage(Update update, MyWebhookBot bot) throws TelegramApiException {
        long chatId = update.getMessage().getChat().getId();
        String welcomeMessage = "Hi bro, ya Petro";

        SendMessage request = Util.toMessage(chatId, welcomeMessage);
        bot.execute(request);
    }

    private void proceedUserMessage(Update update, MyWebhookBot bot) throws TelegramApiException {
        Long code = new Random().nextLong();
        OTP otp = new OTP();
        otp.setOtp(code);
        otp.setUserId(update.getMessage().getChatId());
        otp.setName(update.getMessage().getFrom().getFirstName());
        otpRepository.save(otp);

        SendMessage sendMessage = Util.toMessage(update.getMessage().getChatId(), "Код авторизацii: " + code);
        bot.execute(sendMessage);
    }


    /*
    Збирати повідомлення користувачів, якщо це повідомлення належить до посту, зв'язувати його з постом.
    Якщо це просто написано, тоді в повідомленні, де написано повідомлення - null
     */
    private void checkMessage(Update update, MyWebhookBot bot) throws TelegramApiException {
        Message message = update.getMessage();
        Message reply = message.getReplyToMessage();
        Channel channel = channelService.getChannel(update, bot);

        if (reply != null) {
            if (reply.getFrom().getFirstName().equals("Telegram")) {
                Post post = postService.getPost(channel, reply);
                User user = userService.getUser(message, channel);
                messageService.proceedMessage(channel, post, message, user);
//                ChatMember chatMember = tgBot.execute(new GetChatMember(String.valueOf(reply.getSenderChat().getId()),
//                        user.getTelegramId()));
            }
        } else {
            User user = userService.getUser(message, channel);
            messageService.proceedMessage(channel, null, message, user);
        }
    }

    // з апдейта получити команду яка була визвана
// потім взати код сходити з кодом в базу і получити фідбек від бота
//    private void proceedAdminMessage(Update update, TGBot bot) throws TelegramApiException {
//        String text = update.getMessage().getText();
//        if (text.startsWith("/post ")) {
//            pollingService.proceedPolling(update, bot);
//        }
//    }

    /*
    у юзера є дата останньої активності
    при неактивності протягом 3 діб зробити повідомлення для персонажа щось типу "незабувайте про наш чат"

    якщо персонаж якимось чином відписався також перевіряти це але в вищому приорітеті ніж неактивність
    відсилати йому повідомлення типу "прийди до нас у нас є багато цікових новин"
    написати це повідомлення потрібно після 3-10 діб після відписки.
     */
    public static void checkUser(User user, MyWebhookBot myWebhookBot) throws TelegramApiException {
        if (user.getLeaveTime() != null) {
            if (user.getLeaveTime().isBefore(user.getLeaveTime().minusDays(4))) {
                SendMessage sendMessage = Util.toMessage(user.getTelegramId(), "прийди до нас у нас є багато цікових новин\n" +
                        "https://t.me/+DHDt6LLgj7Y1N2Qy");
                myWebhookBot.execute(sendMessage);
            }
        } else if (user.getLastMessageTime().isBefore(user.getLastMessageTime().minusDays(2))) {
            SendMessage sendMessage = Util.toMessage(user.getTelegramId(), "незабувайте про наш чат\n"
                    + "https://t.me/+DHDt6LLgj7Y1N2Qy");
            myWebhookBot.execute(sendMessage);
        }
    }
}