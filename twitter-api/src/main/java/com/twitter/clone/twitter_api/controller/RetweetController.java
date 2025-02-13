package com.twitter.clone.twitter_api.controller;
import com.twitter.clone.twitter_api.entity.Retweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.DuplicateRetweetException;
import com.twitter.clone.twitter_api.exception.TweetNotFoundException;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.service.RetweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/retweet")
public class RetweetController {

    @Autowired
    private RetweetService retweetService;

    @PostMapping("/{tweetId}")
    public ResponseEntity<Retweet> addRetweet(@PathVariable Long tweetId, @AuthenticationPrincipal User user) {
        try {
            Retweet createdRetweet = retweetService.addRetweet(tweetId, user);
            return ResponseEntity.status(201).body(createdRetweet); // 201 Created
        } catch (DuplicateRetweetException e) {
            return ResponseEntity.status(400).body(null);
        } catch (TweetNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @DeleteMapping("/{retweetId}")
    public ResponseEntity<String> removeRetweet(@PathVariable Long retweetId, @AuthenticationPrincipal User user) {
        try {
            retweetService.removeRetweet(retweetId, user);
            return ResponseEntity.noContent().build();
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(403).body("Bu retweet'i kaldırma yetkiniz yok!");
        } catch (TweetNotFoundException e) {
            return ResponseEntity.status(404).body("Retweet bulunamadı.");
        }
    }
}