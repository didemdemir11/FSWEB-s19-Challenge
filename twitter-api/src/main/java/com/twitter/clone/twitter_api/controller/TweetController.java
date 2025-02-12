package com.twitter.clone.twitter_api.controller;

import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tweet")
public class TweetController {
    @Autowired
    private TweetService tweetService;

    @PostMapping
    public ResponseEntity<Tweet> createTweet(@RequestBody Tweet tweet, @AuthenticationPrincipal User user) {
        tweet.setUser(user);
        Tweet createdTweet = tweetService.createTweet(tweet);
        return ResponseEntity.status(201).body(createdTweet);
    }

    @GetMapping("/findByUserId")
    public ResponseEntity<List<Tweet>> getUserTweets(@AuthenticationPrincipal User user) {
        List<Tweet> tweets = tweetService.getTweetsByUser(user);
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Tweet> getTweetById(@PathVariable Long id) {
        return tweetService.getTweetById(id)
                .map(ResponseEntity::ok)  // 200 OK
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tweet> updateTweet(@PathVariable Long id, @RequestParam String content, @AuthenticationPrincipal User user) {
        Tweet updatedTweet = tweetService.updateTweet(id, content, user);
        return ResponseEntity.ok(updatedTweet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTweet(@PathVariable Long id, @AuthenticationPrincipal User user) {
        tweetService.deleteTweet(id, user);
        return ResponseEntity.noContent().build();
    }
}
