package backend.academy.linktracker.bot.models.commands;

import backend.academy.linktracker.bot.models.ChatStatus;
import backend.academy.linktracker.bot.services.ChatStatusManager;
import backend.academy.linktracker.bot.services.requests.TrackedRequestsSender;
import backend.academy.linktracker.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.models.exceptions.UnknownChatCommandStage;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class UntrackCommand extends BotCommandExec {
    private final TrackedRequestsSender requestsSender;
    private final ChatStatusManager csm;

    public UntrackCommand(TrackedRequestsSender requestsSender, ChatStatusManager csm) {
        super("/untrack", "command to stop tracking a link");
        this.requestsSender = requestsSender;
        this.csm = csm;
    }

    @Override
    public String execute(TelegramBot telegramClient, User user, Chat chat, List<String> messages) {
        String response;
        try {
            if (csm.isDefault(chat.id())) {
                csm.setChatStatus(chat.id(), new ChatStatus(command, "getUrl", new ArrayList<>()));
                response = "Отправьте ссылку которую хотите удалить.";
            } else if (csm.getCommandStatus(chat.id()).getStage().equalsIgnoreCase("getUrl")) {
                csm.cancelStatus(chat.id());
                var url = messages.getFirst();
                requestsSender.removeTrackingUrl(chat.id(), url);
                response = "Ссылка была успешно удалена";
            } else {
                throw new UnknownChatCommandStage("Неизвестная стадия диалога команды.");
            }
        } catch (ScrapperRequestException e) {
            response = e.getMessage() + ": Ссылка не найдена.";
        } catch (Exception e) {
            response = "Произошла ошибка при удалении ссылки";
        }
        return response;
    }
}
