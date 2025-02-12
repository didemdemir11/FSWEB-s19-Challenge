package com.twitter.clone.twitter_api.exception;

public class TweetNotFoundException extends RuntimeException {

    public TweetNotFoundException(String message) {
        super(message);
    }
}