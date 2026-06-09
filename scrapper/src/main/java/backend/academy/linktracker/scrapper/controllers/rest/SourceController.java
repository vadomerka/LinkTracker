package backend.academy.linktracker.scrapper.controllers.rest;

import backend.academy.linktracker.models.LinkDto;
import backend.academy.linktracker.models.http.external.LinkUpdateData;
import backend.academy.linktracker.models.http.internal.AddSourceRequest;
import backend.academy.linktracker.models.http.internal.LinkUpdateRequestItem;
import backend.academy.linktracker.models.http.internal.ListSourcesResponse;
import backend.academy.linktracker.models.http.internal.RemoveSourceRequest;
import backend.academy.linktracker.scrapper.controllers.db.DataController;
import backend.academy.linktracker.scrapper.services.cache.CachedListSourcesService;
import backend.academy.linktracker.scrapper.services.updates.LinkUpdateService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;

@RestController
public class SourceController {
    private final DataController dataController;
    private final CachedListSourcesService cachedListSourcesService;
    private final LinkUpdateService service;

    public SourceController(
            DataController dataController,
            CachedListSourcesService cachedListSourcesService,
            LinkUpdateService service) {
        this.dataController = dataController;
        this.cachedListSourcesService = cachedListSourcesService;
        this.service = service;
    }

    @PostMapping("/tg-chat/{id}")
    ResponseEntity<@NotNull String> addChat(@PathVariable Long id) {
        dataController.addChat(id);
        return ResponseEntity.ok("Чат зарегистрирован");
    }

    @DeleteMapping("/tg-chat/{id}")
    ResponseEntity<@NotNull String> deleteChat(@PathVariable Long id) {
        dataController.removeChat(id);
        return ResponseEntity.ok("Чат успешно удалён");
    }

    @PostMapping("/db-link/{url}")
    ResponseEntity<@NotNull String> addDbLink(@PathVariable String url) {
        dataController.addLink(url);
        return ResponseEntity.ok("Чат зарегистрирован");
    }

    @DeleteMapping("/db-link/{url}")
    ResponseEntity<@NotNull String> deleteDbLink(@PathVariable String url) {
        dataController.removeLink(url);
        return ResponseEntity.ok("Чат успешно удалён");
    }

    @GetMapping("/links")
    ResponseEntity<@NotNull ListSourcesResponse> getLinks(
            @RequestHeader(value = "Tg-Chat-Id", required = false) Long tgChatId,
            @RequestHeader(required = false) String tag) {
        return ResponseEntity.ok(cachedListSourcesService.getChatLinks(tgChatId, tag));
    }

    @PostMapping("/links")
    ResponseEntity<@NotNull LinkDto> addLink(
            @RequestHeader(value = "Tg-Chat-Id") Long tgChatId, @RequestBody AddSourceRequest req) {
        return ResponseEntity.ok(dataController.addLink(tgChatId, req));
    }

    @DeleteMapping("/links")
    ResponseEntity<@NotNull String> deleteLink(
            @RequestHeader(value = "Tg-Chat-Id") Long tgChatId, @RequestBody RemoveSourceRequest req) {
        dataController.removeLink(tgChatId, req);
        return ResponseEntity.ok("Ссылка успешно убрана");
    }

    @PostMapping("/update")
    ResponseEntity<@NotNull String> updateLinks() {
        service.updateLinks();
        return ResponseEntity.ok("Обновление выполнено");
    }

    @PostMapping("/kafka-send-test")
    ResponseEntity<@NotNull String> kafkaSendTestLinks() {
        service.testSend(List.of(new LinkUpdateRequestItem("url", Instant.now(), new LinkUpdateData(List.of()))));
        return ResponseEntity.ok("Обновление выполнено");
    }
}
