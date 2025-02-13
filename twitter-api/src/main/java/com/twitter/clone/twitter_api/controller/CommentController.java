package com.twitter.clone.twitter_api.controller;

import com.twitter.clone.twitter_api.dto.CommentRequest;
import com.twitter.clone.twitter_api.entity.Comment;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.service.CommentService;
import com.twitter.clone.twitter_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Comment> addComment(@Valid @RequestBody CommentRequest request,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        // Kullanıcıyı doğrula
        User user = userService.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı!"));

        // Yorumu kaydet
        Comment createdComment = commentService.addComment(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long id,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userService.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Yetkisiz işlem!"));

        Comment updatedComment = commentService.updateComment(id, request.getContent(), user);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Yetkisiz işlem!"));

        commentService.deleteComment(id, user);
        return ResponseEntity.noContent().build();
    }
}
