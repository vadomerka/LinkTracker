package backend.academy.linktracker.bot.models.commands;

import backend.academy.linktracker.bot.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.bot.services.BotUtils;
import backend.academy.linktracker.bot.services.TrackRequestsSender;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class TrackCommand extends BotCommandExec {
    private final TrackRequestsSender requestsSender;

    public TrackCommand(BotUtils utils, TrackRequestsSender requestsSender) {
        super("/track", "command to start tracking a link", utils);
        this.requestsSender = requestsSender;
    }

    @Override
    public String execute(TelegramBot telegramClient, User user, Chat chat, List<String> messages) {
        String response;
        try {
            var url = messages.getFirst();
            var tags = messages.subList(1, messages.size());

            requestsSender.addTrackingUrl(chat.id(), url, tags, null);
            response = "Ссылка была успешно добавлена";
        } catch (ScrapperRequestException e) {
            response = e.getMessage();
        } catch (Exception e) {
            response = "Произошла ошибка при добавлении ссылки";
        }
        return response;
    }
}
