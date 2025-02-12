package com.twitter.clone.twitter_api;

import com.twitter.clone.twitter_api.entity.Role;
import com.twitter.clone.twitter_api.entity.User;
import com.twitter.clone.twitter_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;

@SpringBootApplication
public class TwitterApiApplication implements CommandLineRunner {
	@Autowired
	private UserRepository userRepository;
	public static void main(String[] args) {
		SpringApplication.run(TwitterApiApplication.class, args);
	}

	@Override
	public void run(String... args) {

		User user = new User("testuser", "password123", "testuser@email.com", Role.USER, new ArrayList<>());

		userRepository.save(user);

		// Kullanıcıları ekrana yazdır
		userRepository.findAll().forEach(System.out::println);
	}
}
