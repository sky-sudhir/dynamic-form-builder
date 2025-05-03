package com.masai.formbuilder.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.masai.formbuilder.config.JwtUtil;
import com.masai.formbuilder.dto.AuthenticationResponse;
import com.masai.formbuilder.dto.MessageResponse;
import com.masai.formbuilder.dto.UserDto;
import com.masai.formbuilder.exception.InvalidCredentialsException;
import com.masai.formbuilder.model.User;
import com.masai.formbuilder.service.OtpService;
import com.masai.formbuilder.service.UserService;
import com.masai.formbuilder.service.event.UserSignupEvent;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ApplicationEventPublisher publisher;
    
    @Autowired
    private OtpService otpService;  
    
    @PostMapping("/register/request-otp")
    public ResponseEntity<?> requestOtp(@Valid @RequestBody UserDto userDTO) {
        if (userService.findByEmail(userDTO.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User already exists"));
        }
        otpService.generateAndSendOtp(userDTO.getEmail(), userDTO.getFirstName()+" "+userDTO.getLastName());
        return ResponseEntity.ok(new MessageResponse("OTP sent to email"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDTO, @RequestParam("otp") String otp) {
    	if (!otpService.validateOtp(userDTO.getEmail(), otp)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalid or expired OTP"));
        }
        User user = userService.register(userDTO);
        String token = jwtUtil.generateToken(user.getEmail());
//        publisher.publishEvent(new UserSignupEvent(this, userDTO.getEmail(), "WELCOME TO STAYEASE !!!", user.getFirstName()+" "+user.getLastName()));
        return ResponseEntity.ok(new MessageResponse("Welcome! You’ve logged in successfully.", AuthenticationResponse.mapToResponse(user, token)));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserDto userDTO) {
        User user = userService.findByEmail(userDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new MessageResponse("Welcome! You’ve logged in successfully.", AuthenticationResponse.mapToResponse(user, token)));
    }
}