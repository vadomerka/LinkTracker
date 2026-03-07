package backend.academy.linktracker.scrapper.controllers;

import backend.academy.linktracker.scrapper.models.requests.ApiErrorResponse;
import backend.academy.linktracker.scrapper.models.exceptions.ChatAlreadyExistsException;
import backend.academy.linktracker.scrapper.models.exceptions.ChatNotFoundException;
import backend.academy.linktracker.scrapper.models.exceptions.SourceNotFoundException;
import backend.academy.linktracker.scrapper.models.exceptions.SourceIsAlreadyTrackedException;
import org.apache.coyote.BadRequestException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Arrays;

@RestControllerAdvice
public class SourceControllerAdvice {
    @ExceptionHandler(ChatAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ResponseEntity<@NotNull ApiErrorResponse> chatAlreadyExistsHandler(ChatAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            new ApiErrorResponse(
                "Чат уже существует",
                HttpStatus.CONFLICT.toString(), ex)
        );
    }

    @ExceptionHandler(ChatNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity<@NotNull ApiErrorResponse> chatNotFoundExceptionHandler(ChatNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ApiErrorResponse(
                "Чат не существует",
                HttpStatus.NOT_FOUND.toString(), ex)
        );
    }

    @ExceptionHandler(SourceIsAlreadyTrackedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ResponseEntity<@NotNull ApiErrorResponse> urlIsAlreadyTrackedHandler(SourceIsAlreadyTrackedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            new ApiErrorResponse(
                "Ссылка уже отслеживается",
                HttpStatus.CONFLICT.toString(), ex)
        );
    }

    @ExceptionHandler(SourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity<@NotNull ApiErrorResponse> sourceNotFoundExceptionHandler(SourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ApiErrorResponse(
                "Ссылка не найдена",
                HttpStatus.NOT_FOUND.toString(), ex)
        );
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<@NotNull ApiErrorResponse> badRequestExceptionHandler(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ApiErrorResponse(
                "Некорректные параметры запроса",
                HttpStatus.BAD_REQUEST.toString(), ex)
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<@NotNull ApiErrorResponse> unknownExceptionHandler(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ApiErrorResponse("Некорректные параметры запроса",
                HttpStatus.BAD_REQUEST.toString(), ex)
        );
    }
}
