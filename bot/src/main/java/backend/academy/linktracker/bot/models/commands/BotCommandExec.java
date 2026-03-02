package backend.academy.linktracker.bot.models.commands;

import backend.academy.linktracker.bot.services.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import org.springframework.stereotype.Component;

@Component
public abstract class BotCommandExec {
    protected final String command;
    protected final String description;
    protected final BotUtils utils;

    public BotCommandExec(String command, String description, BotUtils utils) {
        this.command = command;
        this.description = description;
        this.utils = utils;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public abstract String execute(TelegramBot bot, User user, Chat chat, String[] message);
}
