package backend.academy.linktracker.scrapper.controllers;

import backend.academy.linktracker.scrapper.models.requests.ListSourcesResponse;
import backend.academy.linktracker.scrapper.models.requests.RemoveSourceRequest;
import backend.academy.linktracker.scrapper.models.TrackedSource;
import backend.academy.linktracker.scrapper.models.requests.AddSourceRequest;
import backend.academy.linktracker.scrapper.services.TrackedSourceManager;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class SourceController {
    private final TrackedSourceManager manager;

    public SourceController(TrackedSourceManager manager) {
        this.manager = manager;
    }

    @PostMapping("/tg-chat/{id}")
    ResponseEntity<@NotNull Void> addChat(@RequestHeader Long id) {
        manager.addChat(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tg-chat/{id}")
    ResponseEntity<@NotNull Void> deleteChat(@RequestHeader Long id) {
        manager.removeChat(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/links/{tgChatId}")
    ResponseEntity<@NotNull ListSourcesResponse> getLinks(@RequestHeader Long tgChatId) {
        return ResponseEntity.ok(manager.getLinks(tgChatId));
    }

    @PostMapping("/links/{tgChatId}")
    ResponseEntity<@NotNull TrackedSource> addLink(@RequestHeader Long tgChatId, @RequestBody AddSourceRequest req) {
        return ResponseEntity.ok(manager.addLink(tgChatId, req));
    }

    @DeleteMapping("/links/{tgChatId}")
    ResponseEntity<@NotNull Void> deleteLink(@RequestHeader Long tgChatId, @RequestBody RemoveSourceRequest req) {
        manager.removeLink(tgChatId, req);
        return ResponseEntity.ok().build();
    }
}
