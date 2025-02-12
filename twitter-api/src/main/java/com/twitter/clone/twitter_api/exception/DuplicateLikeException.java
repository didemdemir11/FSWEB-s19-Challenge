package com.twitter.clone.twitter_api.exception;

public class DuplicateLikeException extends RuntimeException {

    public DuplicateLikeException(String message) {
        super(message);
    }
}