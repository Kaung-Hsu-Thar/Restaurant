package com.luv2code.springboot.restaurant.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String recipientEmail, String token) {
        // Generate password reset link
        String resetLink = "https://localhost:8081/reset-password?token=" + token;
        String subject = "Reset Your Password";
        String message = "Click the link to reset your password: " + resetLink;

        // Set up email message
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientEmail);
        email.setSubject(subject);
        email.setText(message);

        // Send email
        mailSender.send(email);
    }
}
