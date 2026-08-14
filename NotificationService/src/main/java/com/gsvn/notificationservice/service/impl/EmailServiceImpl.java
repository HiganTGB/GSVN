package com.gsvn.notificationservice.service.impl;

import com.gsvn.notificationservice.service.EmailService;
import com.gsvn.notificationservice.service.OtpStorageService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;
@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${app.url.reset-password}")
    private String resetUrl;
    @Value("${app.time.reset-password}")
    private int resetTtl;

    private final OtpStorageService otpStorageService;

    public EmailServiceImpl(JavaMailSender mailSender, OtpStorageService otpStorageService) {
        this.mailSender = mailSender;
        this.otpStorageService = otpStorageService;
    }

    public void sendOtp(String email) {
        String otp = generateOtp();
        otpStorageService.storeOtp(email, otp,resetTtl);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP is: " + otp + "\nIt is valid for "+resetTtl+" minutes.");
        mailSender.send(message);
    }

    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // 6-digit OTP
    }
    public void sendResetPasswordEmail(String email, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String fullResetUrl = resetUrl + token;

            helper.setTo(email);
            helper.setSubject("Password Reset Request");

            String htmlContent = String.format(
                    "<div style='font-family: Arial, sans-serif; line-height: 1.6;'>" +
                            "<h2>Password Reset</h2>" +
                            "<p>Hello,</p>" +
                            "<p>We received a request to reset the password for your account. Please click the button below to proceed:</p>" +
                            "<div style='margin: 20px 0;'>" +
                            "  <a href='%s' style='background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Reset Password</a>" +
                            "</div>" +
                            "<p>If the button above doesn't work, you can copy and paste the following link into your browser:</p>" +
                            "<p><a href='%s'>%s</a></p>" +
                            "<p><b>Note:</b> This link will expire in 15 minutes.</p>" +
                            "<p>If you did not request this, please ignore this email.</p>" +
                            "</div>", fullResetUrl, fullResetUrl, fullResetUrl);

            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

}
