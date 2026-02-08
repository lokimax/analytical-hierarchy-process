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
        ("""
            Hello %s,

            Welcome to AHP! Please activate your account by clicking the link below:

            %s/#/activate?token=%s

            This link will expire in 24 hours.

            Best regards,
            AHP Team""")
            .formatted(nickname, frontendUrl, token));

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
        ("""
            Hello %s,

            You have requested to reset your password. Please click the link below to set a new password:

            %s/#/reset-password?token=%s

            This link will expire in 24 hours.

            If you did not request a password reset, please ignore this email.

            Best regards,
            AHP Team""")
            .formatted(nickname, frontendUrl, token));

    mailSender.send(message);
  }
}
