package com.twitter.clone.twitter_api.service;
import com.twitter.clone.twitter_api.entity.Role;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.exception.UserNotFoundException;
import com.twitter.clone.twitter_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User saveUser(User user) {
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
    public User getCurrentUser (UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Giriş yapan kullanıcı bulunamadı."));
    }
    public void deleteUser(Long id,@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı"));


        if (!currentUser.getId().equals(userToDelete.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Bu işlemi gerçekleştirme yetkiniz yok!");
        }
        userRepository.delete(userToDelete);
    }

    public User updateUser(Long id, User updatedUser,@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        User userToUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı"));


        if (!currentUser.getId().equals(userToUpdate.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Bu işlemi gerçekleştirme yetkiniz yok!");
        }


        userToUpdate.setUsername(updatedUser.getUsername());
        userToUpdate.setEmail(updatedUser.getEmail());
        userToUpdate.setPassword(updatedUser.getPassword());
        userToUpdate.setRole(updatedUser.getRole());

        return userRepository.save(userToUpdate);
    }
}