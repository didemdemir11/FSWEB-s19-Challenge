package com.twitter.clone.twitter_api.serviceTest;

import com.twitter.clone.twitter_api.entity.Role;
import com.twitter.clone.twitter_api.entity.Tweet;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.repository.TweetRepository;
import com.twitter.clone.twitter_api.repository.UserRepository;
import com.twitter.clone.twitter_api.service.TweetService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TweetServiceTest {
    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;
    @InjectMocks
    private TweetService tweetService;

    private User testUser;
    private User adminUser;
    private Tweet testTweet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setRole(Role.USER);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("adminUser");
        adminUser.setRole(Role.ADMIN);

        testTweet = new Tweet();
        testTweet.setId(1L);
        testTweet.setContent("Hello Twitter!");
        testTweet.setUser(testUser);
        testTweet.setActive(true);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

    }
    @AfterEach
    void tearDown() {
        Mockito.reset(tweetRepository, userRepository, securityContext, authentication);
    }
    @Test
    void testCreateTweet() {
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(tweetRepository.save(any(Tweet.class))).thenReturn(testTweet);

        Tweet createdTweet = tweetService.createTweet(testTweet);

        assertNotNull(createdTweet);
        assertEquals("Hello Twitter!", createdTweet.getContent());
        assertEquals(testUser, createdTweet.getUser());

        verify(tweetRepository, times(1)).save(any(Tweet.class));
        verify(userRepository, times(1)).findByUsername("testUser");
    }
    @Test
    void testGetTweetById_Success() {
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(testTweet));

        Optional<Tweet> retrievedTweet = tweetService.getTweetById(1L);

        assertTrue(retrievedTweet.isPresent());
        assertEquals("Hello Twitter!", retrievedTweet.get().getContent());
        verify(tweetRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTweetById_NotFound() {
        when(tweetRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Tweet> retrievedTweet = tweetService.getTweetById(2L);

        assertFalse(retrievedTweet.isPresent());
        verify(tweetRepository, times(1)).findById(2L);
    }

    @Test
    void testUpdateTweet_Success() {
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(testTweet));
        when(tweetRepository.save(any(Tweet.class))).thenReturn(testTweet);

        Tweet updatedTweet = tweetService.updateTweet(1L, "Updated Content");

        assertNotNull(updatedTweet);
        assertEquals("Updated Content", updatedTweet.getContent());
        verify(tweetRepository, times(1)).save(any(Tweet.class));
    }

    @Test
    void testUpdateTweet_NotAuthorized() {
        User anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setUsername("anotherUser");

        when(authentication.getName()).thenReturn("anotherUser");
        when(userRepository.findByUsername("anotherUser")).thenReturn(Optional.of(anotherUser));
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(testTweet));

        assertThrows(UnauthorizedAccessException.class, () -> tweetService.updateTweet(1L, "Updated Content"));

        verify(tweetRepository, never()).save(any(Tweet.class));
    }

    @Test
    void testDeleteTweet_SuccessByOwner() {
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(testTweet));

        tweetService.deleteTweet(1L);

        assertFalse(testTweet.isActive());
        verify(tweetRepository, times(1)).save(any(Tweet.class));
    }

    @Test
    void testDeleteTweet_SuccessByAdmin() {
        when(authentication.getName()).thenReturn("adminUser");
        when(userRepository.findByUsername("adminUser")).thenReturn(Optional.of(adminUser));
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(testTweet));

        tweetService.deleteTweet(1L);

        assertFalse(testTweet.isActive());
        verify(tweetRepository, times(1)).save(any(Tweet.class));
    }

    @Test
    void testDeleteTweet_NotAuthorized() {
        User anotherUser = new User();
        anotherUser.setId(3L);
        anotherUser.setUsername("anotherUser");

        when(authentication.getName()).thenReturn("anotherUser");
        when(userRepository.findByUsername("anotherUser")).thenReturn(Optional.of(anotherUser));
        when(tweetRepository.findById(1L)).thenReturn(Optional.of(testTweet));

        assertThrows(UnauthorizedAccessException.class, () -> tweetService.deleteTweet(1L));

        verify(tweetRepository, never()).save(any(Tweet.class));
    }

}
