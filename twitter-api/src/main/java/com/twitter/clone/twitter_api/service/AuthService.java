package com.twitter.clone.twitter_api.service;

import com.twitter.clone.twitter_api.entity.Role;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.DuplicateEmailException;
import com.twitter.clone.twitter_api.exception.DuplicateUsernameException;
import com.twitter.clone.twitter_api.exception.UserNotFoundException;
import com.twitter.clone.twitter_api.repository.UserRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Getter
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String email, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new DuplicateUsernameException("Bu kullanıcı adı zaten alındı.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException("Bu e-posta adresi zaten kullanılıyor.");
        }
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, encodedPassword, email, Role.USER, new ArrayList<>());
        return userRepository.save(user);
    }

    public User loginUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Giriş başarısız! Kullanıcı bulunamadı."));
    }
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Giriş yapan kullanıcı bulunamadı."));
    }
}