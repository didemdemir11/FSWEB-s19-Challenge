package com.twitter.clone.twitter_api.controller;

import com.twitter.clone.twitter_api.entity.Role;
import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tweet")
public class TweetController {
    @Autowired
    private TweetService tweetService;

    @PostMapping
    public ResponseEntity<Tweet> createTweet(@RequestBody Tweet tweet, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        User user = tweetService.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        tweet.setUser(user);
        Tweet createdTweet = tweetService.createTweet(tweet);
        return ResponseEntity.status(201).body(createdTweet);
    }

    @GetMapping("/findByUserId")
    public ResponseEntity<List<Tweet>> getUserTweets(@AuthenticationPrincipal UserDetails principal) {
        User user = tweetService.findUserByUsername(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("Yetkisiz işlem! Kullanıcı bulunamadı."));

        List<Tweet> tweets = tweetService.getTweetsByUser(user);
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Tweet> getTweetById(@PathVariable Long id) {
        return tweetService.getTweetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tweet> updateTweet(@PathVariable Long id, @RequestBody String content) {
        Tweet updatedTweet = tweetService.updateTweet(id, content);
        return ResponseEntity.ok(updatedTweet);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTweet(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        User user = tweetService.findUserByUsername(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("Yetkisiz işlem! Kullanıcı bulunamadı."));

        Tweet tweet = tweetService.getTweetById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tweet bulunamadı"));

        // Kullanıcı Tweet sahibi mi veya Admin mi?
        if (!tweet.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.status(403).body("Bu Tweet'i silme yetkiniz yok!");
        }

        tweetService.deleteTweet(id);
        return ResponseEntity.noContent().build();
    }
}
