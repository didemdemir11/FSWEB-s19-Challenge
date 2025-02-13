package com.twitter.clone.twitter_api.service;
import com.twitter.clone.twitter_api.entity.Retweet;
import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.DuplicateRetweetException;
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
        Optional<Tweet> tweetOpt = tweetRepository.findById(tweetId);
        if (tweetOpt.isEmpty()) {
            throw new TweetNotFoundException("Tweet bulunamadı.");
        }


        Optional<Retweet> existingRetweet = retweetRepository.findByUserIdAndTweetId(requestUser.getId(), tweetId);
        if (existingRetweet.isPresent()) {
            throw new DuplicateRetweetException("Bu tweet zaten retweet edildi.");
        }

        Retweet retweet = new Retweet();
        retweet.setTweet(tweetOpt.get());
        retweet.setUser(requestUser);
        return retweetRepository.save(retweet);
    }

    public List<Retweet> getRetweetsByTweetId(Long tweetId) {
        return retweetRepository.findByTweetId(tweetId);
    }

    public Optional<Retweet> getRetweetByUserAndTweet(Long userId, Long tweetId) {
        return retweetRepository.findByUserIdAndTweetId(userId, tweetId);
    }
    public void removeRetweet(Long retweetId, User requestUser) {
        Optional<Retweet> retweetOpt = retweetRepository.findById(retweetId);

        if (retweetOpt.isPresent()) {
            Retweet retweet = retweetOpt.get();


            if (!retweet.getUser().getId().equals(requestUser.getId())) {
                throw new UnauthorizedAccessException("Bu retweet'i kaldırma yetkiniz yok!");
            }

            retweetRepository.delete(retweet);
        } else {
            throw new TweetNotFoundException("Retweet bulunamadı.");
        }
    }
}