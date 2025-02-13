package com.twitter.clone.twitter_api.repository;
import com.twitter.clone.twitter_api.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByTweetIdOrderByCreatedAtDesc(Long tweetId);
    List<Like> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Like> findByUserId(Long userId);
    Optional<Like> findByUserIdAndTweetId(Long userId, Long tweetId);
}
