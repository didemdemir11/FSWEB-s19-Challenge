package com.twitter.clone.twitter_api.controller;
import com.twitter.clone.twitter_api.entity.Retweet;
import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.DuplicateRetweetException;
import com.twitter.clone.twitter_api.exception.RetweetNotFoundException;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.repository.TweetRepository;
import com.twitter.clone.twitter_api.service.RetweetService;
import com.twitter.clone.twitter_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/retweet")
public class RetweetController {

    @Autowired
    private RetweetService retweetService;

    @Autowired
    private UserService userService;
    @Autowired
    private TweetRepository tweetRepository;

    @PostMapping("/{tweetId}")
    public ResponseEntity<?> addRetweet(@PathVariable Long tweetId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("Yetkisiz işlem! Lütfen giriş yapın.");
        }

        User user = userService.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedAccessException("Kullanıcı bulunamadı!"));

        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new IllegalArgumentException("Tweet bulunamadı!"));

        if (!tweet.isActive()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bu tweet inaktif olduğu için retweet edilemez!");
        }

        try {
            Retweet createdRetweet = retweetService.addRetweet(tweetId, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRetweet);
        } catch (DuplicateRetweetException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bu tweet zaten retweet edildi.");
        }
    }

    @DeleteMapping("/{retweetId}")
    public ResponseEntity<?> removeRetweet(@PathVariable Long retweetId, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Yetkisiz işlem! Lütfen giriş yapın.");
        }

        User user = userService.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedAccessException("Kullanıcı bulunamadı!"));

        try {
            retweetService.removeRetweet(retweetId, user);
            return ResponseEntity.noContent().build();
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bu retweet'i kaldırma yetkiniz yok!");
        } catch (RetweetNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Retweet bulunamadı.");
        }
    }
}