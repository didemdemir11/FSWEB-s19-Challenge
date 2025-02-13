package com.twitter.clone.twitter_api.controller;

import com.twitter.clone.twitter_api.entity.Comment;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment, @AuthenticationPrincipal User user) {
        comment.setUser(user);
        Comment createdComment = commentService.addComment(comment);
        return ResponseEntity.status(201).body(createdComment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody String content, @AuthenticationPrincipal User user) {
        Comment updatedComment = commentService.updateComment(id, content, user);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id, @AuthenticationPrincipal User user) {
        commentService.deleteComment(id, user);
        return ResponseEntity.noContent().build();
    }
}
