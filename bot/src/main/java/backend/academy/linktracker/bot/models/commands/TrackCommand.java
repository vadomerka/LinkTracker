package backend.academy.linktracker.bot.models.commands;

import backend.academy.linktracker.bot.services.BotUtils;
import backend.academy.linktracker.bot.services.TrackRequestsSender;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Objects;

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
        if (messages == null || messages.isEmpty()) {
            response = "Формат команды: /track <url> [tag1] [tag2] [tag3] ...\n" +
                "    <url> - Ссылка для отслеживания." +
                "    [tag] - Опциональные теги.";
        } else {
            var url = messages.getFirst();
            var tags = messages.subList(1, messages.size());

            var result = requestsSender.addTrackingUrl(url, tags, null);
            System.out.println(result.getStatusCode());
            System.out.println(result.getHeaders());
            System.out.println(result.getBody());
            response = "Ссылка была успешно добавлена.";
        }
        return response;
    }
}
