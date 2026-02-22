package backend.academy.linktracker.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TelegramBotUpdateListener implements UpdatesListener {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdateListener.class);
    private final TelegramBot bot;

    public TelegramBotUpdateListener(TelegramBot bot) {
        this.bot = bot;
        bot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> list) {
        for (var u: list) {
            handleUpdate(u);
        }

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void handleUpdate(Update update) {
        if (update == null || update.message() == null || update.message().text() == null) {
            return;
        }

        String text = update.message().text().trim();
        Long chatId = update.message().chat().id();
        LoggingEventBuilder logUpdate = logger.atDebug().addKeyValue("chatId", chatId);

        String message = switch (text.toLowerCase()) {
            case "/start" -> "Hello world!";
            case "/help" -> """
                Available commands:
                /start - Welcome message
                /help - List all commands
                """;
            default -> "Неизвестная команда. Воспользуйтесь /help, чтобы посмотреть список доступных команд.";
        };

        sendMessage(chatId, message);
        logUpdate.log(String.format("Response message - %s", message));
    }

    private void sendMessage(long chatId, String message) {
        SendMessage request = new SendMessage(chatId, message).parseMode(ParseMode.HTML);
        bot.execute(request);
    }
}
