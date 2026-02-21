package backend.academy.linktracker.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TelegramBotUpdateListener implements UpdatesListener {
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

        if ("/start".equalsIgnoreCase(text)) {
            sendMessage(chatId, "Hello world!");
            return;
        }

        if ("/help".equalsIgnoreCase(text)) {
            String message = """
                Available commands:
                /start - Welcome message
                /help - List all commands
                """;
            sendMessage(chatId, message);
            return;
        }

        sendMessage(chatId, "Неизвестная команда. Воспользуйтесь /help, чтобы посмотреть список доступных команд.");
    }

    private void sendMessage(long chatId, String message) {
        SendMessage request = new SendMessage(chatId, message).parseMode(ParseMode.HTML);
        bot.execute(request);
    }
}
