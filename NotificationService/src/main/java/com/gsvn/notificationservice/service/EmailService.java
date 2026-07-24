package com.gsvn.notificationservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OtpStorageService otpStorageService;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String email) {
        String otp = generateOtp();
        otpStorageService.storeOtp(email, otp);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP is: " + otp + "\nIt is valid for 5 minutes.");
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

            // URL trỏ về trang Reset Password của Frontend
            String resetUrl = "http://localhost:5137/reset-password?token=" + token;

            helper.setTo(email);
            helper.setSubject("Yêu cầu khôi phục mật khẩu");

            // Nội dung HTML
            String htmlContent = String.format(
                    "<div style='font-family: Arial, sans-serif; line-height: 1.6;'>" +
                            "<h2>Khôi phục mật khẩu</h2>" +
                            "<p>Chào bạn,</p>" +
                            "<p>Chúng tôi nhận được yêu cầu khôi phục mật khẩu cho tài khoản của bạn. Vui lòng nhấn vào nút bên dưới để thực hiện:</p>" +
                            "<div style='margin: 20px 0;'>" +
                            "  <a href='%s' style='background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Đổi mật khẩu ngay</a>" +
                            "</div>" +
                            "<p>Nếu link trên không hoạt động, bạn có thể copy link sau vào trình duyệt:</p>" +
                            "<p><a href='%s'>%s</a></p>" +
                            "<p><b>Lưu ý:</b> Link này sẽ hết hạn sau 15 phút.</p>" +
                            "<p>Nếu bạn không gửi yêu cầu này, vui lòng bỏ qua email này.</p>" +
                            "</div>", resetUrl, resetUrl, resetUrl);

            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi khi gửi mail: " + e.getMessage());
        }
    }

}
