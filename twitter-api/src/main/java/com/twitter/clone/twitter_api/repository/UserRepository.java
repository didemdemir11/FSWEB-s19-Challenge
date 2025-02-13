package com.twitter.clone.twitter_api.repository;

import com.twitter.clone.twitter_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(String identifier);

    @Query("SELECT u FROM User u WHERE u.username LIKE %:query% OR u.email LIKE %:query%")
    List<User> searchUsers(String query);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM User u JOIN u.following f WHERE u.id = :userId AND f.id = :targetUserId")
    boolean isFollowing(Long userId, Long targetUserId);

    @Query("SELECT COUNT(f) FROM User u JOIN u.followers f WHERE u.id = :userId")
    long getFollowersCount(Long userId);
}
