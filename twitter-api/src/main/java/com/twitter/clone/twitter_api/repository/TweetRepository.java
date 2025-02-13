package com.twitter.clone.twitter_api.repository;

import com.twitter.clone.twitter_api.entity.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends JpaRepository<Tweet,Long> {

    List<Tweet> findByUserIdOrderByCreatedAtDesc(Long userId);

}
