package backend.academy.linktracker.bot.services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;

@Service
public class BotUtils {
    private final TelegramBot bot;

    public BotUtils(TelegramBot bot) {
        this.bot = bot;
    }

    public void sendMessage(long chatId, String message) {
        SendMessage request = new SendMessage(chatId, message).parseMode(ParseMode.HTML);
        bot.execute(request);
    }
}
