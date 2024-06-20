package org.stb.service.impl;

import lombok.RequiredArgsConstructor;
import org.drinkless.tdlib.example.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.stb.bot.MyWebhookBot;
import org.stb.service.PollingService;
import org.stb.util.Util;
import org.stb.entity.Button;
import org.stb.entity.Poll;
import org.stb.repository.PollRepository;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PollingServiceImpl implements PollingService {
    private final PollRepository pollRepository;

    @Override
    @Transactional
    public void pollingConstructor(Poll poll, MyWebhookBot bot) throws TelegramApiException {
        List<Button> buttons = poll.getButtons();
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (Button button : buttons) {
            InlineKeyboardButton action = new InlineKeyboardButton();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            action.setText(button.getText());
            action.setCallbackData(button.getCallbackData());

            rowInline.add(action);
            rowsInline.add(rowInline);
        }
        markupInline.setKeyboard(rowsInline);

        SendMessage message = Util.toMessage(poll.getChannel().getChannelId(), poll.getText());
        message.setReplyMarkup(markupInline);

        bot.execute(message);

        Example.getMessageWithText(poll.getChannel().getChannelId(), 10, poll.getText());
    }

    @Override
    @Transactional
    public void proceedPolling(Update update, MyWebhookBot bot) throws TelegramApiException {
        String text = update.getMessage().getText();
        String[] result = text.split(" ");
        UUID id = UUID.fromString(result[1]);
        if (!pollRepository.existsById(id)) {
            Util.toMessage(update.getMessage().getFrom().getId(), "Ви ввели невірний код");
        } else {
            Poll poll = pollRepository.findFirstById(id);
            pollingConstructor(poll, bot);
        }
    }
}