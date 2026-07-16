package com.project.chitti.config;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.TimeZone;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.project.chitti.entity.Users;
import com.project.chitti.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class AdminInitializer implements CommandLineRunner {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {

		
		if(!userRepository.existsByEmail("admin@chitti.com")) {

			
			Users users = new Users();
			users.setEmail("admin@chitti.com");
			users.setPassword(passwordEncoder.encode("admin@chitti"));
			users.setName("admin");
			users.setRole("ADMIN");
			users.setStatus(true);
			users.setCreatedAt(LocalDateTime.now());
			
			userRepository.save(users);
						
			log.info("Default admin created");
		}
		
		log.info("admin data loaded");
	
	}

}
