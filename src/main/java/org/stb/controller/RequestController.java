package org.stb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.stb.bot.TGBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/bot/eva/")
public class RequestController {
    private final TGBot bot;

    @GetMapping("/code")
    public void getCode(@Param("id") Long id, @Param("code") Long code) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(id);
        message.setText("Код авторизацii: " + code);
        bot.execute(message);
    }
}


