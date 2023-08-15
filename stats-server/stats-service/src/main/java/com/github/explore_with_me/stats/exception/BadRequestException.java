package com.github.explore_with_me.stats.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "start не может быть раньше чем end")
public class BadRequestException extends RuntimeException {

}
