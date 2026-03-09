package backend.academy.linktracker.bot.controllers;

import backend.academy.linktracker.bot.services.BotChatManager;
import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BotController {
    private final BotChatManager manager;

    public BotController(BotChatManager manager) {
        this.manager = manager;
    }

    @PostMapping("/tg-chat/{chatId}")
    ResponseEntity<String> updateChatLinks(@PathVariable Long chatId, @RequestBody LinkUpdateRequest req) {
        manager.processUpdate(chatId, req);
        return ResponseEntity.ok("Обновление обработано");
    }
}
