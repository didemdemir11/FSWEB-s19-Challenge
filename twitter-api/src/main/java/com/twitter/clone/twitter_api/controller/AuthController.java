package com.twitter.clone.twitter_api.controller;

import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.exception.UserNotFoundException;
import com.twitter.clone.twitter_api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User newUser = authService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());
        return ResponseEntity.status(201).body(newUser);
    }
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User loginRequest) {
        return authService.loginUser(loginRequest.getUsername(), loginRequest.getPassword())
                .map(user -> ResponseEntity.ok("Giriş başarılı!"))
                .orElseThrow(() -> new UserNotFoundException("Geçersiz kullanıcı adı veya şifre!"));
    }
}
