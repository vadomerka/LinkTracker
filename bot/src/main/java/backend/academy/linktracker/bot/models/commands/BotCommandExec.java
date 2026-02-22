package backend.academy.linktracker.bot.models.commands;

import backend.academy.linktracker.bot.services.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import org.springframework.stereotype.Component;

@Component
public abstract class BotCommandExec extends BotCommand {
    protected final BotUtils utils;

    public BotCommandExec(String command, String description, BotUtils utils) {
        super(command, description);
        this.utils = utils;
    }

    public abstract String execute(TelegramBot bot, User user, Chat chat, String[] message);
}
