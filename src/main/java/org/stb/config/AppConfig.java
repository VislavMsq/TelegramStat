package org.stb.config;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.stb.bot.BotMethods;
import org.stb.bot.ExceptionDetector;
import org.stb.bot.MyWebhookBot;
import org.stb.initialition.BotConfig;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@AllArgsConstructor
@Configuration
public class AppConfig {
    private final BotConfig botConfig;
    private final BotMethods botMethods;

    @Bean
    public MyWebhookBot myWebhookBot() {
        String path = "https://mriyabothub.chost.com.ua/";

        MyWebhookBot bot = new MyWebhookBot(botMethods, botConfig.getBotName(), botConfig.getToken(), botConfig.getWebhookPath());

        SetWebhook setWebhook = SetWebhook.builder()
                .url(path + botConfig.getWebhookPath())
                .build();

        try {
            bot.setWebhook(setWebhook);
        } catch (TelegramApiException e) {
            LoggerFactory.getLogger(ExceptionDetector.class).error("Error while setting webhook: {}", e.getMessage());
        }

        return bot;
    }
}
