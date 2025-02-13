package com.twitter.clone.twitter_api.service;
import com.twitter.clone.twitter_api.entity.Like;
import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.DuplicateLikeException;
import com.twitter.clone.twitter_api.exception.LikeNotFoundException;
import com.twitter.clone.twitter_api.exception.TweetNotFoundException;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.repository.LikeRepository;
import com.twitter.clone.twitter_api.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private TweetRepository tweetRepository;

    public Like addLike(Long tweetId, User requestUser) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new TweetNotFoundException("Tweet bulunamadı."));

        if (likeRepository.findByUserIdAndTweetId(requestUser.getId(), tweetId).isPresent()) {
            throw new DuplicateLikeException("Bu tweet zaten beğenildi.");
        }

        Like like = Like.builder()
                .tweet(tweet)
                .user(requestUser)
                .build();

        return likeRepository.save(like);
    }
    public List<Like> getLikesByTweetId(Long tweetId) {
        return likeRepository.findByTweetIdOrderByCreatedAtDesc(tweetId);
    }

    public Optional<Like> getLikeByUserAndTweet(Long userId, Long tweetId) {
        return likeRepository.findByUserIdAndTweetId(userId, tweetId);
    }

    public void removeLike(Long tweetId, User requestUser) {
        Like like = likeRepository.findByUserIdAndTweetId(requestUser.getId(), tweetId)
                .orElseThrow(() -> new LikeNotFoundException("Bu tweet için beğeni bulunamadı."));

        likeRepository.delete(like);
    }
}