package com.github.explore_with_me.main.exception.handler;

import java.time.LocalDateTime;


import com.github.explore_with_me.main.exception.model.ApiError;
import com.github.explore_with_me.main.exception.model.BadRequestException;
import com.github.explore_with_me.main.exception.model.ConflictException;
import com.github.explore_with_me.main.exception.model.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionAdviceHandler {

    private final String message = "Запрос составлен некорректно";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ApiError sendBadRequestError(BadRequestException exception) {
        log.warn(exception.getMessage(), exception);
        return new ApiError(HttpStatus.BAD_REQUEST.toString(), exception.getMessage(),
                message, LocalDateTime.now());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError sendMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.warn(exception.getMessage(), exception);
        return new ApiError(HttpStatus.BAD_REQUEST.toString(), exception.getMessage(),
                message, LocalDateTime.now());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError sendPSQLException(ConflictException conflictException) {
        log.warn(conflictException.getMessage(), conflictException);
        return new ApiError(HttpStatus.CONFLICT.toString(), conflictException.getMessage(), message,
                LocalDateTime.now());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError sendNotFoundException(NotFoundException notFoundException) {
        log.warn(notFoundException.getMessage(), notFoundException);
        return new ApiError(HttpStatus.NOT_FOUND.toString(), notFoundException.getMessage(), message,
                LocalDateTime.now());
    }
}

