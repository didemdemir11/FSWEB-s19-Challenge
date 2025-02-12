package com.twitter.clone.twitter_api.service;
import com.twitter.clone.twitter_api.entity.Comment;
import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }
    public Comment updateComment(Long commentId, String newContent, User requestUser) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);

        if (commentOpt.isPresent()) {
            Comment comment = commentOpt.get();


            if (!comment.getUser().getId().equals(requestUser.getId())) {
                throw new UnauthorizedAccessException("Bu yorumu güncelleme yetkiniz yok!");
            }

            comment.setContent(newContent);
            return commentRepository.save(comment);
        } else {
            throw new IllegalArgumentException("Yorum bulunamadı.");
        }
    }
    public List<Comment> getCommentsByTweetId(Long tweetId) {
        return commentRepository.findByTweetIdOrderByCreatedAtDesc(tweetId);
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public void deleteComment(Long commentId, User requestUser) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);

        if (commentOpt.isPresent()) {
            Comment comment = commentOpt.get();
            Tweet tweet = comment.getTweet();

            boolean isCommentOwner = comment.getUser().getId().equals(requestUser.getId());
            boolean isTweetOwner = tweet.getUser().getId().equals(requestUser.getId());

            if (!isCommentOwner && !isTweetOwner) {
                throw new UnauthorizedAccessException("Bu yorumu silme yetkiniz yok!");
            }

            commentRepository.delete(comment);
        } else {
            throw new IllegalArgumentException("Yorum bulunamadı.");
        }
    }
}