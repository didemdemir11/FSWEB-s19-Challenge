package com.twitter.clone.twitter_api.controller;

import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.security.JwtUtil;
import com.twitter.clone.twitter_api.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        String token = authService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body("Kullanıcı başarıyla kaydedildi! JWT Token: " + token);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User loginRequest) {
        String token = authService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok("Giriş başarılı! JWT Token: " + token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        // JWT Stateless olduğu için backend tarafında çıkış yapılmaz.
        return ResponseEntity.ok("Çıkış işlemi frontend tarafında yapılmalıdır.");
    }

    @GetMapping("/check")
    public ResponseEntity<String> checkAuthStatus(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kullanıcı giriş yapmamış.");
        }

        String token = authHeader.substring(7); // "Bearer " kısmını çıkar
        String username = jwtUtil.extractUsername(token);

        return ResponseEntity.ok("Giriş yapan kullanıcı: " + username);
    }
}
