package backend.academy.linktracker.bot.models.commands;

import backend.academy.linktracker.bot.services.ChatStatusManager;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CancelCommand extends BotCommandExec {
    private final ChatStatusManager chatStatusManager;

    public CancelCommand(ChatStatusManager chatStatusManager) {
        super("/cancel", "command to start the bot");
        this.chatStatusManager = chatStatusManager;
    }

    @Override
    public String execute(TelegramBot telegramClient, User user, Chat chat, List<String> messages) {
        String response;
        if (chatStatusManager.isDefault(chat.id())) {
            response = "Нет запущенных команд.";
        } else {
            response = "Команда отменена.";
            chatStatusManager.cancelStatus(chat.id());
        }
        return response;
    }
}
