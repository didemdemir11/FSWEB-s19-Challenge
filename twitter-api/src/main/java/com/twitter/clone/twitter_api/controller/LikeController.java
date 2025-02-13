package com.twitter.clone.twitter_api.controller;
import com.twitter.clone.twitter_api.entity.Like;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.DuplicateLikeException;
import com.twitter.clone.twitter_api.exception.LikeNotFoundException;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.service.LikeService;
import com.twitter.clone.twitter_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like")
public class LikeController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    @PostMapping("/{tweetId}")
    public ResponseEntity<?> addLike(@PathVariable Long tweetId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedAccessException("Yetkisiz işlem! Kullanıcı bulunamadı."));

        try {
            Like createdLike = likeService.addLike(tweetId, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLike); // 201 Created
        } catch (DuplicateLikeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Bu tweet zaten beğenildi.");
        }
    }

    @DeleteMapping("/{tweetId}")
    public ResponseEntity<?> removeLike(@PathVariable Long tweetId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedAccessException("Yetkisiz işlem! Kullanıcı bulunamadı."));

        try {
            likeService.removeLike(tweetId, user);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (LikeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bu tweet için beğeni bulunamadı.");
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bu beğeniyi kaldırma yetkiniz yok!");
        }
    }
}
