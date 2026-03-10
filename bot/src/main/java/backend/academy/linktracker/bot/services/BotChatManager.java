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
        for (var url : req.links()) {
            sb.append("\t");
            sb.append(url);
            sb.append("\n");
        }
        utils.sendMessage(chatId, sb.toString());
    }
}
