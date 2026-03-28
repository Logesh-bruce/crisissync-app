package com.crisissync.service;

/**
 * Placeholder — implement email service here.
 * e.g., sendWelcomeEmail, sendPasswordReset, sendNotification.
 */
public interface EmailService {
    void sendWelcomeEmail(String to, String name);
    void sendPasswordResetEmail(String to, String resetToken);
}
