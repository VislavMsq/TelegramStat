package org.stb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.stb.bot.MyWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@RestController
public class TelegramBotController {
    MyWebhookBot bot;

    @PostMapping("/telegram-wh/eva")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        bot.onWebhookUpdateReceived(update);
        return null;
    }
}
