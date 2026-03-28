package com.crisissync.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Override
    public void sendWelcomeEmail(String to, String name) {
        String subject = "Welcome to CrisisSync!";
        String body = "<h2>Hello, " + name + "!</h2>"
                + "<p>Your account has been successfully created on CrisisSync.</p>"
                + "<p>Thank you for joining us.</p>";
        sendHtmlEmail(to, subject, body);
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        String subject = "CrisisSync — Password Reset Request";
        String body = "<h2>Password Reset</h2>"
                + "<p>Use the token below to reset your password (expires in 15 minutes):</p>"
                + "<h3>" + resetToken + "</h3>"
                + "<p>If you did not request this, please ignore this email.</p>";
        sendHtmlEmail(to, subject, body);
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
