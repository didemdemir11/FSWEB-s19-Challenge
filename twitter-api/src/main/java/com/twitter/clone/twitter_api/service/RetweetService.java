package com.twitter.clone.twitter_api.service;
import com.twitter.clone.twitter_api.entity.Retweet;
import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.DuplicateRetweetException;
import com.twitter.clone.twitter_api.exception.RetweetNotFoundException;
import com.twitter.clone.twitter_api.exception.TweetNotFoundException;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.repository.RetweetRepository;
import com.twitter.clone.twitter_api.repository.TweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RetweetService {

    @Autowired
    private RetweetRepository retweetRepository;

    @Autowired
    private TweetRepository tweetRepository;

    public Retweet addRetweet(Long tweetId, User requestUser) {
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new TweetNotFoundException("Tweet bulunamadı."));

        if (retweetRepository.existsByUserIdAndTweetId(requestUser.getId(), tweetId)) {
            throw new DuplicateRetweetException("Bu tweet zaten retweet edildi.");
        }

        Retweet retweet = new Retweet();
        retweet.setTweet(tweet);
        retweet.setUser(requestUser);
        return retweetRepository.save(retweet);
    }

    public List<Retweet> getRetweetsByTweetId(Long tweetId) {
        return retweetRepository.findByTweetIdOrderByCreatedAtDesc(tweetId);
    }

    public Optional<Retweet> getRetweetByUserAndTweet(Long userId, Long tweetId) {
        return retweetRepository.findByUserIdAndTweetId(userId, tweetId);
    }
    public void removeRetweet(Long tweetId, User requestUser) {
        Retweet retweet = retweetRepository.findByUserIdAndTweetId(requestUser.getId(), tweetId)
                .orElseThrow(() -> new RetweetNotFoundException("Bu retweet bulunamadı."));

        retweetRepository.delete(retweet);
    }
}