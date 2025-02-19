package com.twitter.clone.twitter_api.service;

import com.twitter.clone.twitter_api.entity.Role;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.DuplicateEmailException;
import com.twitter.clone.twitter_api.exception.DuplicateUsernameException;
import com.twitter.clone.twitter_api.exception.UserNotFoundException;
import com.twitter.clone.twitter_api.repository.UserRepository;
import com.twitter.clone.twitter_api.security.JwtUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Getter
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    @Autowired
    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String registerUser(String username, String email, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new DuplicateUsernameException("Bu kullanıcı adı zaten alındı.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new DuplicateEmailException("Bu e-posta adresi zaten kullanılıyor.");
        }

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, encodedPassword, email, Role.USER, new ArrayList<>());
        userRepository.save(user);

        return jwtUtil.generateToken(username); // Kullanıcıya JWT token döndür
    }

    public String loginUser(String usernameOrEmail, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usernameOrEmail, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                    .orElseThrow(() -> new UserNotFoundException("Giriş başarısız! Kullanıcı bulunamadı."));

            return jwtUtil.generateToken(user.getUsername()); // JWT Token döndür

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Geçersiz kullanıcı adı veya şifre!");
        }
    }
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Giriş yapan kullanıcı bulunamadı."));
    }
}