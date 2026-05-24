package backend.academy.linktracker.bot.models.commands;

import backend.academy.linktracker.bot.models.ChatStatus;
import backend.academy.linktracker.bot.services.ChatStatusManager;
import backend.academy.linktracker.bot.services.requests.TrackedRequestsSender;
import backend.academy.linktracker.models.exceptions.IllegalChatStatusCommand;
import backend.academy.linktracker.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.models.exceptions.UnknownChatCommandStage;
import backend.academy.linktracker.models.http.internal.ListSourcesResponse;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class ListCommand extends BotCommandExec {
    private static final String STOP_TAGS_COMMAND = "/next";
    private final TrackedRequestsSender requestsSender;
    private final ChatStatusManager csm;

    public ListCommand(TrackedRequestsSender requestsSender, ChatStatusManager csm) {
        super("/list", "command to stop tracking a link");
        this.requestsSender = requestsSender;
        this.csm = csm;
    }

    @NotNull
    private static String makeResponse(ListSourcesResponse res) {
        var sb = new StringBuilder("Список ссылок:\n");
        for (var ts : res.links()) {
            sb.append(String.format("\turl: %s;", ts.url()));
            if (ts.tags() != null) {
                sb.append("\n\ttags: ");
                for (var tsTag : ts.tags()) {
                    sb.append(String.format("%s; ", tsTag));
                }
            }
            //            if (ts.filters() != null) {
            //                sb.append("\n\tfilters: ");
            //                for (var tsF : ts.filters()) {
            //                    sb.append(String.format("%s; ", tsF));
            //                }
            //            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String execute(TelegramBot telegramClient, User user, Chat chat, List<String> messages) {
        String response;
        try {
            if (csm.isDefault(chat.id())) {
                csm.setChatStatus(chat.id(), new ChatStatus(command, "addTag", new ArrayList<>()));
                return String.format(
                        "Отправьте тег для получения ссылок с ним " + "или \"%s\" чтобы сразу вывести все ссылки.",
                        STOP_TAGS_COMMAND);
            }

            var chatStatus = csm.getCommandStatus(chat.id());
            if (!chatStatus.getCmdName().equalsIgnoreCase(command)) {
                throw new IllegalChatStatusCommand("Команда не может обработать переданный статус чата.");
            }
            return switch (chatStatus.getStage()) {
                case "addTag" -> addTagStage(chat.id(), messages);
                case "getTracked" -> getTrackedStage(chat.id());
                default -> throw new UnknownChatCommandStage("Неизвестная стадия диалога команды.");
            };
        } catch (ScrapperRequestException e) {
            response = e.getMessage();
        } catch (Exception e) {
            response = String.format("Произошла ошибка при получении списка ссылок: %s", e.getMessage());
        }
        return response;
    }

    private String addTagStage(Long chatId, List<String> messages) {
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("Неверный формат сообщения.");
        }
        csm.setStage(chatId, "getTracked");
        if (!messages.getFirst().equalsIgnoreCase(STOP_TAGS_COMMAND)) {
            csm.addData(chatId, messages.getFirst());
            return "Тег добавлен. " + getTrackedStage(chatId);
        }
        return "Теги не будут добавлены.\n" + getTrackedStage(chatId);
    }

    private String getTrackedStage(Long chatId) {
        var data = csm.getCommandStatus(chatId).getChatData();
        var tag = data.isEmpty() ? null : data.getFirst();

        csm.cancelStatus(chatId);
        var res = requestsSender.getTrackingUrls(chatId, tag).getBody();
        if (res == null) throw new ScrapperRequestException("Список ссылок не был получен");

        String response;
        if (res.size() > 0) {
            response = makeResponse(res);
        } else {
            if (tag == null) {
                response = "Список ссылок пуст";
            } else {
                response = "Ссылки с данными тегами не найдены";
            }
        }

        return response;
    }
}
