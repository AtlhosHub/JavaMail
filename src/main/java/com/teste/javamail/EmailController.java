package com.teste.javamail;

import com.teste.javamail.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/ler-emails")
    public String lerEmails() {
        try {
            emailService.readEmails();
            return "Emails lidos com sucesso!";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Erro ao ler os emails!";
        }
    }
}
