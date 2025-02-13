package com.twitter.clone.twitter_api.controller;
import com.twitter.clone.twitter_api.entity.Like;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.DuplicateLikeException;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like")
public class LikeController {
    @Autowired
    private LikeService likeService;

    @PostMapping("/{tweetId}")
    public ResponseEntity<Like> addLike(@PathVariable Long tweetId, @AuthenticationPrincipal User user) {
        try {
            Like createdLike = likeService.addLike(tweetId, user);
            return ResponseEntity.status(201).body(createdLike); // 201 Created
        } catch (DuplicateLikeException e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @DeleteMapping("/{tweetId}")
    public ResponseEntity<String> removeLike(@PathVariable Long tweetId, @AuthenticationPrincipal User user) {
        try {
            likeService.removeLike(tweetId, user);
            return ResponseEntity.noContent().build();
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(403).body("Bu beğeniyi kaldırma yetkiniz yok!");
        }
    }
}
