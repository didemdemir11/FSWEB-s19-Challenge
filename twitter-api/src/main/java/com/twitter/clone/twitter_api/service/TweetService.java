package com.twitter.clone.twitter_api.service;
import com.twitter.clone.twitter_api.entity.Role;
import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.TweetNotFoundException;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.repository.TweetRepository;
import com.twitter.clone.twitter_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TweetService {

    @Autowired
    private TweetRepository tweetRepository;
    @Autowired
    private UserRepository userRepository;

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedAccessException("Giriş yapan kullanıcı bulunamadı."));
    }

    public Tweet createTweet(Tweet tweet) {
        User currentUser = getAuthenticatedUser();
        tweet.setUser(currentUser);
        return tweetRepository.save(tweet);
    }

    public List<Tweet> getTweetsByUser(User requestUser) {
        return tweetRepository.findByUserIdOrderByCreatedAtDesc(requestUser.getId());
    }

    public Optional<Tweet> getTweetById(Long id) {
        return tweetRepository.findById(id);
    }

    public List<Tweet> getTweetsByUserId(Long userId) {
        return tweetRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void deleteTweet(Long tweetId) {
        User currentUser = getAuthenticatedUser();
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new TweetNotFoundException("Tweet bulunamadı."));

        if (!tweet.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Bu tweet'i silme yetkiniz yok!");
        }

        tweetRepository.delete(tweet);
    }
    public Tweet updateTweet(Long tweetId, String newContent) {
        User currentUser = getAuthenticatedUser();
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new TweetNotFoundException("Tweet bulunamadı."));

        if (!tweet.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Bu tweet'i güncelleme yetkiniz yok!");
        }

        tweet.setContent(newContent);
        return tweetRepository.save(tweet);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}