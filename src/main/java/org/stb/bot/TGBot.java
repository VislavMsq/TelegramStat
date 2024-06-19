package org.stb.bot;

import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.stb.initialition.BotConfig;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.GetWebhookInfo;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.WebhookInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.ExecutionException;

@Component
@AllArgsConstructor
//public class TGBot extends TelegramLongPollingBot {
//    private final BotConfig botConfig;
//    private final BotMethods botMethods;
//    @Override
//    public void onUpdateReceived(Update update) {
//        try {
//            botMethods.handleUpdate(update, this);
//        } catch (TelegramApiException | ExecutionException | InterruptedException e) {
//            LoggerFactory.getLogger(ExceptionDetector.class).error(e.getMessage());
//        }
//    }
//
//    @Override
//    public String getBotUsername() {
//        return botConfig.getBotName();
//    }
//
//    @Override
//    public String getBotToken() {
//        return botConfig.getToken();
//    }
//}

//@AllArgsConstructor
public class TGBot extends TelegramWebhookBot {

    private final BotConfig botConfig;
    private final BotMethods botMethods;

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        try {
            botMethods.handleUpdate(update, this);
        } catch (TelegramApiException | ExecutionException | InterruptedException e) {
            LoggerFactory.getLogger(ExceptionDetector.class).error(e.getMessage());
        }

        return null;
    }

    @Override
    public String getBotPath() {
        return botConfig.getWebhookPath();
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }


    public void init() {
        // Ініціалізація та встановлення вебхука
//        MyWebhookBot bot = new MyWebhookBot(botConfig.getToken(), "YOUR_BOT_USERNAME", "YOUR_BOT_PATH");

        SetWebhook setWebhook = new SetWebhook();
        setWebhook.setUrl("https://mriyabothub.chost.com.ua/" + botConfig.getWebhookPath());

        try {
            this.execute(setWebhook);
            WebhookInfo webhookInfo = this.execute(new GetWebhookInfo());
            System.out.println("Webhook Info: " + webhookInfo);
        } catch (TelegramApiException e) {
            LoggerFactory.getLogger(ExceptionDetector.class).error(e.getMessage());
        }
    }


//    public MyWebhookBot(String botToken, String botUsername, String botPath) {
//        this.botToken = botToken;
//        this.botUsername = botUsername;
//        this.botPath = botPath;
}

//    @Override
//    public String getBotToken() {
//        return botConfig.getToken();
//    }

//    @Override
//    public void onUpdateReceived(Update update) {
//        try {
//            botMethods.handleUpdate(update, this);
//        } catch (TelegramApiException | ExecutionException | InterruptedException e) {
//            LoggerFactory.getLogger(ExceptionDetector.class).error(e.getMessage());
//        }
//    }

//    @Override
//    public String getBotUsername() {
//        return botUsername;
//    }
//
//    @Override
//    public String getBotPath() {
//        return botPath;
//    }
//
//    public static void main(String[] args) {
//        // Ініціалізація та встановлення вебхука
//        MyWebhookBot bot = new MyWebhookBot("YOUR_BOT_TOKEN", "YOUR_BOT_USERNAME", "YOUR_BOT_PATH");
//
//        SetWebhook setWebhook = new SetWebhook();
//        setWebhook.setUrl("https://yourdomain.com/webhook");
//
//        try {
//            bot.execute(setWebhook);
//            WebhookInfo webhookInfo = bot.execute(new org.telegram.telegrambots.meta.api.methods.GetWebhookInfo());
//            System.out.println("Webhook Info: " + webhookInfo);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//}