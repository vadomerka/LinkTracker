package backend.academy.linktracker.bot.models.commands;

import backend.academy.linktracker.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.bot.services.BotUtils;
import backend.academy.linktracker.bot.services.requests.ChatRequestsSender;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class StartCommand extends BotCommandExec {
    private final ChatRequestsSender requestsSender;

    public StartCommand(BotUtils utils, ChatRequestsSender requestsSender) {
        super("/start", "command to start the bot", utils);
        this.requestsSender = requestsSender;
    }

    @Override
    public String execute(TelegramBot telegramClient, User user, Chat chat, List<String> messages) {
        String response;
        try {
            response = requestsSender.addChat(chat.id()).getBody();
        } catch (ScrapperRequestException e) {
            response = e.getMessage();
        } catch (Exception e) {
            response = "Произошла ошибка при регистрации чата.";
        }
        return response;
    }
}
