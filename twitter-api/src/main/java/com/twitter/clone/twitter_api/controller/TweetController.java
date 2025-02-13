package com.twitter.clone.twitter_api.controller;

import com.twitter.clone.twitter_api.entity.Role;
import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/tweet")
public class TweetController {
    @Autowired
    private TweetService tweetService;

    @PostMapping
    public ResponseEntity<Tweet> createTweet(@RequestBody @Valid Tweet tweet, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        User user = tweetService.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı!"));

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
    public ResponseEntity<Tweet> updateTweet(@PathVariable Long id, @RequestBody @Valid Tweet updatedTweet,
                                             @AuthenticationPrincipal UserDetails principal) {
        User user = tweetService.findUserByUsername(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Yetkisiz işlem!"));

        Tweet tweet = tweetService.getTweetById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tweet bulunamadı."));

        // Kullanıcı, kendi tweetini mi güncelliyor?
        if (!tweet.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Tweet updated = tweetService.updateTweet(id, updatedTweet.getContent());
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        User user = tweetService.findUserByUsername(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Yetkisiz işlem!"));

        Tweet tweet = tweetService.getTweetById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tweet bulunamadı."));

        // Kullanıcı kendi tweetini mi siliyor veya admin mi?
        if (!tweet.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        tweetService.deleteTweet(id);
        return ResponseEntity.noContent().build();
    }

}
