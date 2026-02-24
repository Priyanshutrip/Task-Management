package com.taskmanager.auth_service.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.taskmanager.auth_service.dto.LoginRequest;
import com.taskmanager.auth_service.dto.RegisterRequest;
import com.taskmanager.auth_service.entity.User;
import com.taskmanager.auth_service.repository.UserRepository;
import com.taskmanager.auth_service.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public void register(RegisterRequest request) {

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        userRepository.save(user);
    }
    
    public String login(LoginRequest request) {
    	User user=userRepository.findByEmail(request.getEmail())
    			.orElseThrow(() -> new RuntimeException("Invalid email or password"));
    	
    	if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
    		throw new RuntimeException("Invalid email or password");
    	}
    	
    	return jwtUtil.generateToken(user.getEmail());
    }
}
