package stb.bot;

import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import stb.initialition.BotConfig;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.ExecutionException;

@Component
@AllArgsConstructor
public class TGBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final BotMethods botMethods;
    @Override
    public void onUpdateReceived(Update update) {
        try {
            botMethods.handleUpdate(update, this);
        } catch (TelegramApiException | ExecutionException | InterruptedException e) {
            LoggerFactory.getLogger(ExceptionDetector.class).error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}
