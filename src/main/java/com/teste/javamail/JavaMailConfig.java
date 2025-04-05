package com.teste.javamail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.mail.Session;
import java.util.Properties;

@Configuration
public class JavaMailConfig {

    @Bean
    public Session mailSession() {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imap.host", "imap.gmail.com");
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");

        return Session.getInstance(properties);
    }
}
