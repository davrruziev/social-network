package api.giybat.uz.service;

import api.giybat.uz.dto.MessageDTO;
import api.giybat.uz.enums.AppLanguage;
import api.giybat.uz.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmailSendingService {

    @Value("${spring.mail.username}")
    private String fromAccount;
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendRegistrationEmail(String email, Integer profileId, AppLanguage lang){
        String subject = "Registration Confirmation";
        String body = " Please link to link completing registration:  http://localhost:8080/auth/registration/email-verification/" + JwtUtil.encode(profileId)+"?lang="+lang.name();
        System.out.println(JwtUtil.encode(profileId));
        sendMail(email, subject, body);
    }

    private void sendMail(String email, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAccount);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        CompletableFuture.runAsync(() -> {
            javaMailSender.send(message);
        });

    }

}
