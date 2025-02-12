package com.twitter.clone.twitter_api.exception;

public class DuplicateRetweetException extends RuntimeException{
    public DuplicateRetweetException(String message) {
        super(message);
    }
}
