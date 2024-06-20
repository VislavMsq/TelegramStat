package org.stb.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.stb.bot.BotMethods;
import org.stb.bot.ExceptionDetector;
import org.stb.bot.TGBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.ExecutionException;

@AllArgsConstructor
@RestController
public class TelegramBotController {
    BotMethods botMethods;
    TGBot bot;

    @PostMapping("/telegram-wh/eva")
    public ResponseEntity<String> handleUpdate(@RequestBody Update update) {
        try {
            botMethods.handleUpdate(update, bot);
        } catch (TelegramApiException | ExecutionException | InterruptedException e) {
            LoggerFactory.getLogger(ExceptionDetector.class).error("Случилась ошибка во время выполнения handleUpdate: ", e);
        }
        return ResponseEntity.ok("Update received");
    }
}
