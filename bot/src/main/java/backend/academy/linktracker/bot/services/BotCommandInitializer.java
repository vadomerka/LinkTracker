package backend.academy.linktracker.bot.services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandScope;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandScopeAllChatAdministrators;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandScopeAllGroupChats;
import com.pengrad.telegrambot.model.botcommandscope.BotCommandScopeAllPrivateChats;
import com.pengrad.telegrambot.request.SetMyCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class BotCommandInitializer {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdateListener.class);
    private final TelegramBot bot;
    private final BotCommandService commandService;
    private final List<BotCommandScope> scopes = new ArrayList<>(List.of(
        new BotCommandScopeAllPrivateChats(),
        new BotCommandScopeAllGroupChats(),
        new BotCommandScopeAllChatAdministrators()
    ));

    public BotCommandInitializer(TelegramBot bot, BotCommandService commandService) {
        this.bot = bot;
        this.commandService = commandService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initBotCommands() {
        logger.info("initBotCommands");

        var arr = commandService.getAllCommands();
        BotCommand[] commands = arr.stream()
            .map((bce) -> new BotCommand(bce.command(), bce.description()))
            .toList()
            .toArray(new BotCommand[0]);
        for (var scope: scopes) {
            bot.execute(new SetMyCommands(commands).scope(scope));
        }
    }
}
