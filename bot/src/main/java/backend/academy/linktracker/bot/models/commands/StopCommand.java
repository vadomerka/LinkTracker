package backend.academy.linktracker.bot.models.commands;

import backend.academy.linktracker.bot.services.requests.ChatRequestsSender;
import backend.academy.linktracker.models.exceptions.ScrapperRequestException;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StopCommand extends BotCommandExec {
    private final ChatRequestsSender requestsSender;

    public StopCommand(ChatRequestsSender requestsSender) {
        super("/stop", "command to stop the bot");
        this.requestsSender = requestsSender;
    }

    @Override
    public String execute(TelegramBot telegramClient, User user, Chat chat, List<String> messages) {
        String response;
        try {
            response = requestsSender.removeChat(chat.id()).getBody();
        } catch (ScrapperRequestException e) {
            response = e.getMessage();
        } catch (Exception e) {
            response = "Произошла ошибка при удалении чата.";
        }
        return response;
    }
}
