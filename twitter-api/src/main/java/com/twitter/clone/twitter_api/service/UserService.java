package com.twitter.clone.twitter_api.service;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.UnauthorizedAccessException;
import com.twitter.clone.twitter_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteUser(Long id, Long requestUserId) {
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Yetkilendirme kontrolü (Sadece admin veya kullanıcı kendi kaydını silebilir)
            if (!user.getId().equals(requestUserId)) {
                throw new UnauthorizedAccessException("Bu işlemi gerçekleştirme yetkiniz yok!");
            }
            userRepository.delete(user);
        } else {
            throw new IllegalArgumentException("Kullanıcı bulunamadı");
        }
    }
    public User updateUser(Long id, User updatedUser, Long requestUserId) {
        Optional<User> userOpt = userRepository.findById(id);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Yetkilendirme kontrolü (Sadece admin veya kullanıcı kendi kaydını güncelleyebilir)
            if (!user.getId().equals(requestUserId)) {
                throw new UnauthorizedAccessException("Bu işlemi gerçekleştirme yetkiniz yok!");
            }

            // Kullanıcı bilgilerini güncelle
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());
            user.setRole(updatedUser.getRole());

            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Kullanıcı bulunamadı");
        }
    }
}