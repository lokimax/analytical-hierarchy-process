package de.x132.ahp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails.
 *
 * @author Max Wick
 */
@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;

  @Value("${app.mail.from:noreply@ahp.de}")
  private String from;

  @Value("${app.activation.frontend-url:http://localhost:4200}")
  private String frontendUrl;

  /**
   * Sends an activation email to the user.
   *
   * @param email the user's email address
   * @param nickname the user's nickname
   * @param token the activation token
   */
  public void sendActivationEmail(String email, String nickname, String token) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(from);
    message.setTo(email);
    message.setSubject("Activate your AHP account");
    message.setText(
        String.format(
            "Hello %s,\n\n"
                + "Welcome to AHP! Please activate your account by clicking the link below:\n\n"
                + "%s/#/activate?token=%s\n\n"
                + "This link will expire in 24 hours.\n\n"
                + "Best regards,\n"
                + "AHP Team",
            nickname, frontendUrl, token));

    mailSender.send(message);
  }

  /**
   * Sends a password reset email to the user.
   *
   * @param email the user's email address
   * @param nickname the user's nickname
   * @param token the password reset token
   */
  public void sendPasswordResetEmail(String email, String nickname, String token) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(from);
    message.setTo(email);
    message.setSubject("Password Reset Request");
    message.setText(
        String.format(
            "Hello %s,\n\n"
                + "You have requested to reset your password. Please click the link below to set a new password:\n\n"
                + "%s/#/reset-password?token=%s\n\n"
                + "This link will expire in 24 hours.\n\n"
                + "If you did not request a password reset, please ignore this email.\n\n"
                + "Best regards,\n"
                + "AHP Team",
            nickname, frontendUrl, token));

    mailSender.send(message);
  }
}
