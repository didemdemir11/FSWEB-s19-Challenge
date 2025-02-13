package com.twitter.clone.twitter_api.repository;

import com.twitter.clone.twitter_api.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTweetIdOrderByCreatedAtDesc(Long tweetId);
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Comment> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT c FROM Comment c WHERE c.id = :commentId AND c.tweet.user.id = :tweetOwnerId")
    Optional<Comment> findByIdAndTweetUserId(Long commentId, Long tweetOwnerId);
}
