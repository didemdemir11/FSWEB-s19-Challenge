package com.twitter.clone.twitter_api.service;
import com.twitter.clone.twitter_api.dto.CommentRequest;
import com.twitter.clone.twitter_api.entity.Comment;
import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.CommentNotFoundException;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.repository.CommentRepository;
import com.twitter.clone.twitter_api.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TweetRepository tweetRepository;

    public Comment addComment(CommentRequest request, User user) {
        // Tweet ID ile tweeti bul
        Tweet tweet = tweetRepository.findById(request.getTweetId())
                .orElseThrow(() -> new RuntimeException("Tweet bulunamadı!"));

        // Comment nesnesini oluştur
        Comment comment = Comment.builder()
                .content(request.getContent())
                .user(user)
                .tweet(tweet)
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }
    public Comment updateComment(Long commentId, String newContent, User requestUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Yorum bulunamadı."));

        if (!comment.getUser().getId().equals(requestUser.getId())) {
            throw new UnauthorizedAccessException("Bu yorumu güncelleme yetkiniz yok!");
        }

        comment.setContent(newContent);
        return commentRepository.save(comment);
    }
    public List<Comment> getCommentsByTweetId(Long tweetId) {
        return commentRepository.findByTweetIdOrderByCreatedAtDesc(tweetId);
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Yorum bulunamadı."));
    }

    public void deleteComment(Long commentId, User requestUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Yorum bulunamadı."));

        Tweet tweet = comment.getTweet();
        boolean isCommentOwner = comment.getUser().getId().equals(requestUser.getId());
        boolean isTweetOwner = tweet.getUser().getId().equals(requestUser.getId());

        if (!isCommentOwner && !isTweetOwner) {
            throw new UnauthorizedAccessException("Bu yorumu silme yetkiniz yok!");
        }

        commentRepository.delete(comment);
    }
}