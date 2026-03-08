package backend.academy.linktracker.bot.models.commands;

import backend.academy.linktracker.bot.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.bot.services.BotUtils;
import backend.academy.linktracker.bot.services.requests.TrackedRequestsSender;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ListCommand extends BotCommandExec {
    private final TrackedRequestsSender requestsSender;

    public ListCommand(BotUtils utils, TrackedRequestsSender requestsSender) {
        super("/untrack", "command to stop tracking a link", utils);
        this.requestsSender = requestsSender;
    }

    @Override
    public String execute(TelegramBot telegramClient, User user, Chat chat, List<String> messages) {
        String response;
        try {
            var url = messages.getFirst();
            var tags = messages.subList(1, messages.size());

            requestsSender.removeTrackingUrl(chat.id(), url);
            response = "Ссылка была успешно удалена";
        } catch (ScrapperRequestException e) {
            response = e.getMessage();
        } catch (Exception e) {
            response = "Произошла ошибка при добавлении ссылки";
        }
        return response;
    }
}
