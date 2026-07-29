package backend.academy.linktracker.bot.services;

import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import org.springframework.stereotype.Service;

@Service
public class BotChatManager {
    private final BotUtils utils;

    public BotChatManager(BotUtils utils) {
        this.utils = utils;
    }

    public void processUpdate(Long chatId, LinkUpdateRequest req) {
        var sb = new StringBuilder("Произошло обновление по следующим ссылкам:\n");
        utils.sendMessage(chatId, sb.toString());
        for (var data : req.links()) {
            utils.sendMessage(chatId, "\n\t" + data.url() + ":");
            for (var upd : data.data().data()) {
                sb = new StringBuilder();
                sb.append("\t\t");
                sb.append(upd);
                sb.append("\n");
                utils.sendMessage(chatId, sb.toString());
            }
            sb.append("\n");
        }
    }
}
