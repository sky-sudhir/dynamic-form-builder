package com.masai.formbuilder.service.eventListeners;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.masai.formbuilder.service.EmailService;
import com.masai.formbuilder.service.event.UserSignupEvent;

@Component
public class UserSignupEventListener implements ApplicationListener<UserSignupEvent> {

    @Autowired
    private EmailService emailService;

    @Async
    @Override
    public void onApplicationEvent(UserSignupEvent event) {
        try {
			emailService.sendHtmlEmail(event.getEmail(), event.getSubject(), event.getName(), event.getOtp());
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
