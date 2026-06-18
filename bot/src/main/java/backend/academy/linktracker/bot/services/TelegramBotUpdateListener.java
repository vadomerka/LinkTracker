package backend.academy.linktracker.bot.services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.stereotype.Service;

@Service
public class TelegramBotUpdateListener implements UpdatesListener {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdateListener.class);
    private final TelegramBot bot;
    private final BotUtils utils;
    private final BotCommandService commandService;
    private final ChatStatusManager csm;

    public TelegramBotUpdateListener(
            TelegramBot bot, BotUtils utils, BotCommandService commandService, ChatStatusManager csm) {
        this.bot = bot;
        this.utils = utils;
        this.commandService = commandService;
        this.csm = csm;
        bot.setUpdatesListener(this);
        logger.info("setUpdatesListener");
    }

    @Override
    public int process(List<Update> list) {
        for (var u : list) {
            handleUpdate(u);
        }

        return CONFIRMED_UPDATES_ALL;
    }

    private void handleUpdate(Update update) {
        if (update == null || update.message() == null || update.message().text() == null) {
            return;
        }

        String text = update.message().text().trim();
        Long chatId = update.message().chat().id();
        LoggingEventBuilder logUpdate = logger.atDebug().addKeyValue("chatId", chatId);

        String response;

        var parsedLine = Arrays.stream(text.split(" ")).toList();
        String cmdName;

        cmdName = parsedLine.getFirst().toLowerCase();
        if (!commandService.isCancel(cmdName) && !csm.isDefault(chatId)) {
            cmdName = csm.getCommandStatus(chatId).getCmdName();
        }
        var cmd = commandService.getCommand(cmdName);

        if (cmd == null) {
            response = "Неизвестная команда. Воспользуйтесь /help, чтобы посмотреть список доступных команд.";
        } else {
            response =
                    cmd.execute(bot, update.message().from(), update.message().chat(), parsedLine);
        }

        utils.sendMessage(chatId, response);
        logUpdate.log(String.format("Response message - %s", response));
    }
}
