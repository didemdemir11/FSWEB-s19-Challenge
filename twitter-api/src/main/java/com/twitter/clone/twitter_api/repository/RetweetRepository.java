package com.twitter.clone.twitter_api.repository;

import com.twitter.clone.twitter_api.entity.Retweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RetweetRepository extends JpaRepository<Retweet, Long> {
    List<Retweet> findByTweetIdOrderByCreatedAtDesc(Long tweetId);
    List<Retweet> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Retweet> findByTweetId(Long tweetId);
    List<Retweet> findByUserId(Long userId);
    Optional<Retweet> findByUserIdAndTweetId(Long userId, Long tweetId);
    @Query("SELECT COUNT(r) FROM Retweet r WHERE r.tweet.id = :tweetId")
    long countRetweetsByTweetId(Long tweetId);
}