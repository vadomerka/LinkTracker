package backend.academy.linktracker.bot.controllers;

import backend.academy.linktracker.bot.services.BotChatManager;
import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpBotController {
    private final BotChatManager manager;

    public HttpBotController(BotChatManager manager) {
        this.manager = manager;
    }

    @PostMapping("/tg-chat/{chatId}")
    ResponseEntity<String> updateChatLinks(@RequestBody LinkUpdateRequest req) {
        manager.processUpdate(req);
        return ResponseEntity.ok("Обновление обработано");
    }
}
