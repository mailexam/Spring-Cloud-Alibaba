package com.example.demo.web;

import com.example.demo.mail.MailService;
import jakarta.mail.MessagingException;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    public record SendRequest(String to, String subject, String body) {
        public SendRequest {
            if (to == null || to.isBlank()) {
                to = "user@example.test";
            }
            if (subject == null || subject.isBlank()) {
                subject = "Spring Cloud Alibaba + Mailexam";
            }
            if (body == null || body.isBlank()) {
                body = "Mailexam test from Spring Cloud Alibaba";
            }
        }
    }

    @PostMapping("/mail/test")
    public Map<String, String> sendTest(@RequestBody SendRequest request) throws MessagingException {
        mailService.sendTest(request.to(), request.subject(), request.body());
        return Map.of("status", "ok");
    }
}
