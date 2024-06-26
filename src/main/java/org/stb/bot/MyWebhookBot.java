package org.stb.bot;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.stb.initialition.BotConfig;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class MyWebhookBot extends TelegramWebhookBot {

    private final BotMethods botMethods;
    private final BotConfig botConfig;
    //    private final String botUsername;
//    private final String botToken;
//    private final String botPath;
    public static Boolean ON_REGISTER = false;
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final String telegram_api = "https://api.telegram.org/bot";

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
        ON_REGISTER = true;
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    public void deleteWebhook() {
        ResponseEntity<String> response = restTemplate.postForEntity(String.format("%s%s/deleteWebhook?drop_pending_updates=true", telegram_api, botConfig.getToken()), null, String.class);
        LoggerFactory.getLogger(WebhookHandler.class).info(response.getBody());

    }

    public void setWebhook() {
        ResponseEntity<String> response = restTemplate.postForEntity(String.format("%s%s/setWebhook?url=%s%s", telegram_api, botConfig.getToken(), botConfig.getRootPath(), botConfig.getWebhookPath()), null, String.class);
        LoggerFactory.getLogger(WebhookHandler.class).info(response.getBody());
    }

    public void webhookInfo() {
        ResponseEntity<String> response = restTemplate.getForEntity(String.format("%s%s/getWebhookInfo", telegram_api, botConfig.getToken()), String.class);
        LoggerFactory.getLogger(WebhookHandler.class).info(response.getBody());
    }
}