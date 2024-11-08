package com.luv2code.springboot.restaurant.service;

import com.luv2code.springboot.restaurant.dto.BaseResponse;
import com.luv2code.springboot.restaurant.dto.EmailDto;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.javaMailSender = mailSender;
    }

    public BaseResponse sendPasswordCreateEmail(EmailDto emailDto, String password) {
        String subject = "Welcome to Charlie's Restaurant - Your Account Information";

        // Compose the email message including the generated password
        String message = "Hello,\n\nYour account has been successfully created! Here are your login details:\n\n" +
                "Username: " + emailDto.getRecipientEmail() + "\n" +
                "Temporary Password: " + password + "\n\n" +
                "Please log in and change your password at your earliest convenience.\n\n" +
                "Regards,\nCharlie's Restaurant Team";

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");

            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(emailDto.getRecipientEmail());
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(message, false);  // `false` indicates plain text

            javaMailSender.send(mimeMessage);

            log.info("Password email sent successfully to: {}", emailDto.getRecipientEmail());
            return new BaseResponse("000", "Email sent successfully", emailDto.getRecipientEmail());
        } catch (Exception e) {
            log.error("Error while sending password email: {}", e.getMessage());
            return new BaseResponse("999", "Failed to send email", null);
        }
    }


}
