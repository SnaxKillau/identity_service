package gearhead.identityservice.service;

import gearhead.identityservice.entity.PasswordForgotToken;
import gearhead.identityservice.entity.UserCredential;
import gearhead.identityservice.repository.PasswordForgotRepository;
import gearhead.identityservice.repository.UserCredentialRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class PasswordService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserCredentialRepository repository;

    @Autowired
    private PasswordForgotRepository passwordForgotRepository;


    public void ForgetPasswordToken(String email){
        UserCredential user = repository.findByEmail(email).orElse(null);
        if(user == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Email isn't exist");
        }
        else{
            //mail config
            String resetToken = UUID.randomUUID().toString();
            PasswordForgotToken token = new PasswordForgotToken();
            token.setToken(resetToken);
            token.setUser(user);
            token.setExpiryDate(LocalDateTime.now().plus(10, ChronoUnit.MINUTES));
            passwordForgotRepository.save(token);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper;
            System.out.println(resetToken);
            try {
                helper = new MimeMessageHelper(mimeMessage, true);
                helper.setFrom("noreply@baeldung.com");
                helper.setTo(email);
                helper.setSubject("Test");
                String resetUrl = "http://localhost:3000/forgot-password/token/" + resetToken;
                String htmlContent = "<p>Hello,</p><p>Please click <a href=\"" + resetUrl + "\">here</a> to reset your password.</p>";
                helper.setText(htmlContent, true);
                javaMailSender.send(mimeMessage);
            } catch (MessagingException e) {
                e.printStackTrace(); // Handle exception accordingly
            }
        }
    }
}
