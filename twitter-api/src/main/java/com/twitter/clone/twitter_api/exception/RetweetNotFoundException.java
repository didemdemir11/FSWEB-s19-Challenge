package com.twitter.clone.twitter_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RetweetNotFoundException extends RuntimeException {
    public RetweetNotFoundException(String message) {
        super(message);
    }
}