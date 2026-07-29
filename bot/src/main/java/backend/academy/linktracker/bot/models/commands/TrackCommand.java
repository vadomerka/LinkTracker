package backend.academy.linktracker.bot.models.commands;

import backend.academy.linktracker.bot.models.ChatStatus;
import backend.academy.linktracker.bot.services.ChatStatusManager;
import backend.academy.linktracker.bot.services.requests.TrackedRequestsSender;
import backend.academy.linktracker.models.exceptions.IllegalChatStatusCommand;
import backend.academy.linktracker.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.models.exceptions.UnknownChatCommandStage;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TrackCommand extends BotCommandExec {
    private static final String STOP_TAGS_COMMAND = "/send";
    private final List<String> YES_TAGS_MESSAGES = new ArrayList<>(List.of("yes", "y", "да", "д"));
    private final TrackedRequestsSender requestsSender;
    private final ChatStatusManager csm;

    public TrackCommand(TrackedRequestsSender requestsSender, ChatStatusManager csm) {
        super("/track", "command to start tracking a link");
        this.requestsSender = requestsSender;
        this.csm = csm;
    }

    @Override
    public String execute(TelegramBot telegramClient, User user, Chat chat, List<String> messages) {
        String response;
        try {
            if (csm.isDefault(chat.id())) {
                csm.setChatStatus(chat.id(), new ChatStatus(command, "getUrl", new ArrayList<>()));
                return "Отправьте ссылку для отслеживания.";
            }

            var chatStatus = csm.getCommandStatus(chat.id());
            if (!chatStatus.getCmdName().equalsIgnoreCase(command)) {
                throw new IllegalChatStatusCommand("Команда не может обработать переданный статус чата.");
            }
            return switch (chatStatus.getStage()) {
                case "getUrl" -> getUrlStage(chat.id(), messages);
                case "addTags" -> addTagsStage(chat.id(), messages);
                case "getTags" -> getTagsStage(chat.id(), messages);
                case "sendTracked" -> sendTrackedStage(chat.id());
                default -> throw new UnknownChatCommandStage("Неизвестная стадия диалога команды.");
            };
        } catch (ScrapperRequestException e) {
            response = e.getMessage();
        } catch (Exception e) {
            response = "Произошла ошибка при добавлении ссылки";
        }
        return response;
    }

    private String getUrlStage(Long chatId, List<String> messages) {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("Неверный формат сообщения.");
        }
        var url = messages.getFirst();
        csm.setStage(chatId, "addTags");
        csm.addData(chatId, url);
        return String.format("Получена ссылка %s%n" + "Добавить теги к ссылке? y/n", url);
    }

    private String addTagsStage(Long chatId, List<String> messages) {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("Неверный формат сообщения.");
        }
        if (YES_TAGS_MESSAGES.contains(messages.getFirst().toLowerCase())) {
            csm.setStage(chatId, "getTags");
            return "Введите теги, которые хотите добавить к ссылке. "
                    + String.format("Чтобы сохранить ссылку отправьте %s", STOP_TAGS_COMMAND);
        }
        csm.setStage(chatId, "sendTracked");
        return "Теги не будут добавлены.\n" + sendTrackedStage(chatId);
    }

    private String getTagsStage(Long chatId, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            throw new IllegalArgumentException("Неверный формат сообщения.");
        }
        if (tags.getFirst().equalsIgnoreCase(STOP_TAGS_COMMAND)) {
            csm.setStage(chatId, "sendTracked");
            return sendTrackedStage(chatId);
        }
        csm.addAllData(chatId, tags);
        return "Теги добавлены.";
    }

    private String sendTrackedStage(Long chatId) {
        var data = csm.getCommandStatus(chatId).getChatData();
        var url = data.getFirst();
        var tags = data.subList(1, data.size());

        // Перед отправкой запроса нужно отменить команду, тк в его обработчике ошибок этого нет.
        csm.cancelStatus(chatId);
        requestsSender.addTrackingUrl(chatId, url, tags, null);

        return "Ссылка добавлена в отслеживаемые.";
    }
}
