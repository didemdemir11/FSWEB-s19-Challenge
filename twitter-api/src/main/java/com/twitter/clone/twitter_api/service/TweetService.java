package com.twitter.clone.twitter_api.service;
import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TweetService {

    @Autowired
    private TweetRepository tweetRepository;

    public Tweet createTweet(Tweet tweet) {
        return tweetRepository.save(tweet);
    }

    public List<Tweet> getTweetsByUser(User requestUser) {
        return tweetRepository.findByUserIdOrderByCreatedAtDesc(requestUser.getId()); // Tarihe göre sıralı tweetler
    }

    public Optional<Tweet> getTweetById(Long id) {
        return tweetRepository.findById(id);
    }

    public List<Tweet> getTweetsByUserId(Long userId) {
        return tweetRepository.findByUserIdOrderByCreatedAtDesc(userId); // Tarihe göre sıralı tweetler
    }

    public void deleteTweet(Long tweetId, User requestUser) {
        Optional<Tweet> tweetOpt = tweetRepository.findById(tweetId);

        if (tweetOpt.isPresent()) {
            Tweet tweet = tweetOpt.get();
            if (!tweet.getUser().getId().equals(requestUser.getId())) {
                throw new UnauthorizedAccessException("Bu tweet'i silme yetkiniz yok!");
            }
            tweetRepository.delete(tweet);
        } else {
            throw new RuntimeException("Tweet bulunamadı.");
        }
    }
    public Tweet updateTweet(Long tweetId, String newContent, User requestUser) {
        Optional<Tweet> tweetOpt = tweetRepository.findById(tweetId);

        if (tweetOpt.isPresent()) {
            Tweet tweet = tweetOpt.get();
            if (!tweet.getUser().getId().equals(requestUser.getId())) {
                throw new UnauthorizedAccessException("Bu tweet'i güncelleme yetkiniz yok!");
            }
            tweet.setContent(newContent);
            return tweetRepository.save(tweet);
        } else {
            throw new IllegalArgumentException("Tweet bulunamadı.");
        }
    }
}