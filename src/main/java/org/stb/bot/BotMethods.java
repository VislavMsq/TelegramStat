package org.stb.bot;

import lombok.RequiredArgsConstructor;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.example.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stb.entity.*;
import org.stb.repository.*;
import org.stb.service.*;
import org.stb.util.Util;
import org.stb.entity.*;
import org.stb.repository.*;
import org.stb.service.*;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class BotMethods {
    //    private static final Logger LOGGER = LoggerFactory.getLogger(BotMethods.class);
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
    public void handleUpdate(Update update, TGBot bot) throws TelegramApiException, ExecutionException, InterruptedException {
        if (update.hasCallbackQuery()) {
            callBackService.proceedCallBack(update.getCallbackQuery(), bot);
            return;
        }
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.getFrom().getFirstName().equals("Telegram")) {
                GetChat getChat = new GetChat();
                getChat.setChatId(update.getMessage().getChatId());
                Chat chat = bot.execute(getChat);
                System.out.println(chat.getId());

                WebStats webStats = new WebStats();

                getChat.setChatId(chat.getLinkedChatId());
                chat = bot.execute(getChat);

                System.out.println("====channelId=====");
                System.out.println(chat.getId());


                List<TdApi.Message> messageList = Example.getChatHistory(chat.getId(), 10).join();

                TdApi.Message msg = Example.getMessageWithText(chat.getId(), 10, message.getText()).join();

                webStats.setChannelId(chat.getId());
                webStats.setGlobalId(msg.id);
                webStats.setLocalId(update.getMessage().getMessageId());
                webStats.setViewCount(0);

                webStats.setReactionCount(0);
                webStats.setReplyCount(0);

                webStats.setLastUpdateReaction(LocalDateTime.now());
                webStats.setLastUpdateReply(LocalDateTime.now());
                webStats.setLastUpdateView(LocalDateTime.now());

                webStatsRepository.save(webStats);

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


    private void proceedUserMessage(Update update, TGBot bot) throws TelegramApiException {
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
    private void checkMessage(Update update, TGBot bot) throws TelegramApiException {
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
    public static void checkUser(User user, TGBot tgBot) throws TelegramApiException {
        if (user.getLeaveTime() != null) {
            if (user.getLeaveTime().isBefore(user.getLeaveTime().minusDays(4))) {
                SendMessage sendMessage = Util.toMessage(user.getTelegramId(), "прийди до нас у нас є багато цікових новин\n" +
                        "https://t.me/+DHDt6LLgj7Y1N2Qy");
                tgBot.execute(sendMessage);
            }
        } else if (user.getLastMessageTime().isBefore(user.getLastMessageTime().minusDays(2))) {
            SendMessage sendMessage = Util.toMessage(user.getTelegramId(), "незабувайте про наш чат\n"
                    + "https://t.me/+DHDt6LLgj7Y1N2Qy");
            tgBot.execute(sendMessage);
        }
    }
}