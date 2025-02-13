package com.twitter.clone.twitter_api.service;
import com.twitter.clone.twitter_api.entity.Role;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.exception.UserNotFoundException;
import com.twitter.clone.twitter_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı"));
    }
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public User getCurrentUser (UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Giriş yapan kullanıcı bulunamadı."));
    }
    public void deleteUser(Long id,UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı"));


        if (!currentUser.getId().equals(userToDelete.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Bu işlemi gerçekleştirme yetkiniz yok!");
        }
        userRepository.delete(userToDelete);
    }

    public User updateUser(Long id, User updatedUser,UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı"));


        if (!currentUser.getId().equals(userToUpdate.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Bu işlemi gerçekleştirme yetkiniz yok!");
        }


        userToUpdate.setUsername(updatedUser.getUsername());
        userToUpdate.setEmail(updatedUser.getEmail());
        if (!updatedUser.getPassword().isEmpty()) {
            userToUpdate.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        userToUpdate.setRole(updatedUser.getRole());

        return userRepository.save(userToUpdate);
    }
    public void followUser(Long currentUserId, Long targetUserId) {
        User currentUser = getUserById(currentUserId);
        User targetUser = getUserById(targetUserId);

        if (userRepository.isFollowing(currentUserId, targetUserId)) {
            throw new RuntimeException("Zaten takip ediliyor.");
        }

        currentUser.getFollowing().add(targetUser);
        targetUser.getFollowers().add(currentUser);

        userRepository.save(currentUser);
        userRepository.save(targetUser);
    }
    public void unfollowUser(Long currentUserId, Long targetUserId) {
        User currentUser = getUserById(currentUserId);
        User targetUser = getUserById(targetUserId);

        if (!userRepository.isFollowing(currentUserId, targetUserId)) {
            throw new RuntimeException("Takip edilmeyen bir kullanıcı takipten çıkarılamaz.");
        }

        currentUser.getFollowing().remove(targetUser);
        targetUser.getFollowers().remove(currentUser);

        userRepository.save(currentUser);
        userRepository.save(targetUser);
    }

    public long getFollowersCount(Long userId) {
        return userRepository.getFollowersCount(userId);
    }

    public long getFollowingCount(Long userId) {
        return getUserById(userId).getFollowing().size();
    }

    public List<User> searchUsers(String query) {
        return userRepository.searchUsers(query);
    }


}