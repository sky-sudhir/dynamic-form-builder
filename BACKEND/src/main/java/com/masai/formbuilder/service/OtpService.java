package com.masai.formbuilder.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.masai.formbuilder.service.event.UserSignupEvent;

@Service
public class OtpService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ApplicationEventPublisher publisher;

    public void generateAndSendOtp(String email, String name) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP
        redisTemplate.opsForValue().set("OTP_" + email, otp, 5, TimeUnit.MINUTES); // Store in Redis

        // Send OTP email using existing event mechanism
        publisher.publishEvent(new UserSignupEvent(this, email, "Your OTP for formBuilder", name, otp));
    }

    public boolean validateOtp(String email, String otp) {
        String storedOtp = redisTemplate.opsForValue().get("OTP_" + email);
        return otp.equals(storedOtp);
    }

    public void clearOtp(String email) {
        redisTemplate.delete("OTP_" + email);
    }
}
