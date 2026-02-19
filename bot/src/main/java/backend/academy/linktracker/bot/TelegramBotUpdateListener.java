package backend.academy.linktracker.bot;

import backend.academy.linktracker.bot.properties.TelegramProperties;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TelegramBotUpdateListener implements UpdatesListener {
    private final TelegramBot bot;

    private final TelegramProperties properties;

    public TelegramBotUpdateListener(TelegramBot bot, TelegramProperties properties) {
        this.bot = bot;
        this.properties = properties;
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
            String message = "start message";

            SendMessage request = new SendMessage(chatId, message)
                .parseMode(ParseMode.HTML);
            SendResponse sendResponse = bot.execute(request);
            return;
        }

        if ("/help".equalsIgnoreCase(text)) {
//            telegramBot.send(token, new SendMessageRequest(chatId,
//                """
//                Available commands:
///start - Welcome message
///help - List all commands
///echo <text> - Repeat what you say
//                """));
            return;
        }

        if (text.startsWith("/echo")) {
//            String echo = text.replaceFirst("/echo", "").trim();
//            telegramBot.sendMessage(token, new SendMessageRequest(chatId,
//                echo.isEmpty() ? "You didn’t provide any text." : echo));
            return;
        }

//        telegramBot.sendMessage(token, new SendMessageRequest(chatId,
//            "Unknown command. Type /help for a list of available commands."));
    }
}
