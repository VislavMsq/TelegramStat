package stb.service.impl;

import lombok.RequiredArgsConstructor;
import drinkless.tdlib.example.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stb.bot.TGBot;
import stb.entity.Button;
import stb.entity.Poll;
import stb.repository.PollRepository;
import stb.service.PollingService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static stb.util.Util.toMessage;

@Service
@RequiredArgsConstructor
public class PollingServiceImpl implements PollingService {
    private final PollRepository pollRepository;

    @Override
    @Transactional
    public void pollingConstructor(Poll poll, TGBot bot) throws TelegramApiException {
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

        SendMessage message = toMessage(poll.getChannel().getChannelId(), poll.getText());
        message.setReplyMarkup(markupInline);

        bot.execute(message);

        Example.getMessageWithText(poll.getChannel().getChannelId(), 10, poll.getText());
    }

    @Override
    @Transactional
    public void proceedPolling(Update update, TGBot bot) throws TelegramApiException {
        String text = update.getMessage().getText();
        String[] result = text.split(" ");
        UUID id = UUID.fromString(result[1]);
        if (!pollRepository.existsById(id)) {
            toMessage(update.getMessage().getFrom().getId(), "Ви ввели невірний код");
        } else {
            Poll poll = pollRepository.findFirstById(id);
            pollingConstructor(poll, bot);
        }
    }
}