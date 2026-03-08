package backend.academy.linktracker.scrapper.controllers;

import backend.academy.linktracker.models.TrackedSource;
import backend.academy.linktracker.models.ListSourcesResponse;
import backend.academy.linktracker.models.RemoveSourceRequest;
import backend.academy.linktracker.models.AddSourceRequest;
import backend.academy.linktracker.scrapper.services.TrackedSourceManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SourceController {
    private final TrackedSourceManager manager;

    public SourceController(TrackedSourceManager manager) {
        this.manager = manager;
    }

    @PostMapping("/tg-chat/{id}")
    ResponseEntity<@NotNull String> addChat(@PathVariable Long id) {
        manager.addChat(id);
        return ResponseEntity.ok("Чат зарегистрирован");
    }

    @DeleteMapping("/tg-chat/{id}")
    ResponseEntity<@NotNull String> deleteChat(@PathVariable Long id) {
        manager.removeChat(id);
        return ResponseEntity.ok("Чат успешно удалён");
    }

    @GetMapping("/links")
    ResponseEntity<@NotNull ListSourcesResponse> getLinks(@RequestHeader Long tgChatId) {
        return ResponseEntity.ok(manager.getLinks(tgChatId));
    }

    @PostMapping("/links")
    ResponseEntity<@NotNull TrackedSource> addLink(@RequestHeader Long tgChatId, @RequestBody AddSourceRequest req) {
        return ResponseEntity.ok(manager.addLink(tgChatId, req));
    }

    @DeleteMapping("/links")
    ResponseEntity<@NotNull String> deleteLink(@RequestHeader Long tgChatId, @RequestBody RemoveSourceRequest req) {
        manager.removeLink(tgChatId, req);
        return ResponseEntity.ok("Ссылка успешно убрана");
    }
}
