package backend.academy.linktracker.scrapper.controllers;

import backend.academy.linktracker.scrapper.models.exceptions.UrlIsAlreadyTrackedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UrlIsAlreadyTrackedAdvice {
    @ExceptionHandler(UrlIsAlreadyTrackedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String urlIsAlreadyTrackedHandler(UrlIsAlreadyTrackedException ex) {
        return ex.getMessage();
    }
}
