package backend.academy.linktracker.bot.models.commands;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import java.util.List;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public abstract class BotCommandExec {
    protected final String command;
    protected final String description;

    public BotCommandExec(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public abstract String execute(TelegramBot bot, User user, Chat chat, List<String> message);
}
