package backend.academy.linktracker.bot.models.commands;

import backend.academy.linktracker.bot.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.bot.services.BotUtils;
import backend.academy.linktracker.bot.services.requests.TrackedRequestsSender;
import backend.academy.linktracker.models.ListSourcesResponse;
import backend.academy.linktracker.models.TrackedSource;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ListCommand extends BotCommandExec {
    private final TrackedRequestsSender requestsSender;

    public ListCommand(BotUtils utils, TrackedRequestsSender requestsSender) {
        super("/list", "command to stop tracking a link", utils);
        this.requestsSender = requestsSender;
    }

    @Override
    public String execute(TelegramBot telegramClient, User user, Chat chat, List<String> messages) {
        String response;
        try {
            var tag = messages.isEmpty() ? null : messages.getFirst();
            var res = requestsSender.getTrackingUrls(chat.id(), tag).getBody();
            if (res == null) throw new ScrapperRequestException("Список ссылок не был получен");

            if (res.size() > 0) {
                response = makeResponse(res);
            } else {
                if (tag == null) {
                    response = "Список ссылок пуст";
                } else {
                    response = "Ссылки с данными тегами не найдены";
                }
            }
        } catch (ScrapperRequestException e) {
            response = e.getMessage();
        } catch (Exception e) {
            response = String.format("Произошла ошибка при получении списка ссылок: %s", e.getMessage());
        }
        return response;
    }

    @NotNull
    private static String makeResponse(ListSourcesResponse res) {
        var sb = new StringBuilder("Список ссылок:\n");
        for (var ts: res.links()) {
            sb.append(String.format("\turl: %s;", ts.url()));
            if (ts.tags() != null) {
                sb.append("\n\ttags: ");
                for (var tsTag : ts.tags()) {
                    sb.append(String.format("%s; ", tsTag));
                }
            }
            if (ts.filters() != null) {
                sb.append("\n\tfilters: ");
                for (var tsF : ts.filters()) {
                    sb.append(String.format("%s; ", tsF));
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
