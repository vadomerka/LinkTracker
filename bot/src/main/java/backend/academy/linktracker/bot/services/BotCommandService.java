package backend.academy.linktracker.bot.services;

import backend.academy.linktracker.bot.models.commands.BotCommandExec;
import backend.academy.linktracker.bot.models.commands.HelpCommand;
import com.pengrad.telegrambot.model.BotCommand;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class BotCommandService {
    private final List<BotCommandExec> commands;
    private final Map<String, BotCommandExec> commandMap;

    public BotCommandService(List<BotCommandExec> commands) {
        this.commands = commands;
        this.commandMap = commands.stream().collect(Collectors.toMap(BotCommand::command, Function.identity()));
        var hc = (HelpCommand) getCommand("/help");
        hc.setCommandService(this);
    }

    public BotCommandService getInstance() {
        return this;
    }

    public BotCommandExec getCommand(String commandName) {
        return commandMap.get(commandName);
    }

    public List<BotCommandExec> getAllCommands() {
        return commands;
    }

    public String commandsToString() {
        var hmb = new StringBuilder();
        hmb.append("Available commands:\n");
        for (var c : getAllCommands()) {
            hmb.append(String.format("%s - %s%n", c.command(), c.description()));
        }
        return hmb.toString();
    }
}
