package backend.academy.linktracker.bot.models.commands;

import backend.academy.linktracker.bot.services.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class StartCommand extends BotCommandExec {

    public StartCommand(BotUtils utils) {
        super("/start", "command to start the bot", utils);
    }

    @Override
    public String execute(TelegramBot telegramClient, User user, Chat chat, List<String> messages) {
        String userName = user.firstName() + " " + user.lastName();
        return String.format("Hello, %s!", userName);
    }
}
