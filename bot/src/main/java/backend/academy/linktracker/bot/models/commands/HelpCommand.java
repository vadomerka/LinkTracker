package backend.academy.linktracker.bot.models.commands;

import backend.academy.linktracker.bot.services.BotCommandService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class HelpCommand extends BotCommandExec {
    private transient BotCommandService commandService;

    @Lazy
    public HelpCommand() {
        super("/help", "command to start the bot");
    }

    public void setCommandService(BotCommandService commandService) {
        this.commandService = commandService;
    }

    @Override
    public String execute(TelegramBot telegramClient, User user, Chat chat, List<String> messages) {
        return commandService.commandsToString();
    }
}
