package backend.academy.linktracker.scrapper.controllers;

import backend.academy.linktracker.scrapper.models.TrackedSource;
import backend.academy.linktracker.scrapper.models.TrackedSourceDTO;
import backend.academy.linktracker.scrapper.services.TrackedSourceManager;
import jdk.jshell.Snippet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class SourceController {
    private final TrackedSourceManager manager;

    public SourceController(TrackedSourceManager manager) {
        this.manager = manager;
    }

    @GetMapping("/list")
    List<TrackedSource> all() {
        return manager.getAll();
    }

    @PostMapping("/track")
    TrackedSource addTracked(@RequestBody TrackedSourceDTO dto) {
        return manager.add(dto);
    }

    @GetMapping("/error")
    String error() {
        return "error occurred";
    }
}
