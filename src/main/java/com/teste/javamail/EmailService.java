package com.teste.javamail;



import org.springframework.stereotype.Service;

import javax.mail.*;
import java.io.IOException;
import java.util.Properties;

@Service
public class EmailService {

    public void readEmails() throws MessagingException {
        // Propriedades de configuração para conectar via IMAP
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imap.host", "imap.gmail.com");
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");

        // Criando a sessão de email
        Session session = Session.getInstance(properties);

        // Conectando ao servidor IMAP do Gmail
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", "acdnbvilaformosa@gmail.com", "pugu lgto wmuz evqs");

        // Obtendo a caixa de entrada
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY); // Modo somente leitura

        // Lendo as mensagens
        Message[] messages = inbox.getMessages();
        System.out.println("Total de mensagens: " + messages.length);

        for (Message message : messages) {
            System.out.println("Assunto: " + message.getSubject());
            System.out.println("De: " + message.getFrom()[0]);
            System.out.println("Data: " + message.getSentDate());

            // Obtendo o conteúdo da mensagem
            try {
                Object content = message.getContent();
                if (content instanceof String) {
                    // Se o conteúdo for uma String (geralmente texto simples)
                    System.out.println("Conteúdo: " + content);
                } else if (content instanceof Multipart) {
                    // Caso seja uma mensagem multipart (com anexos ou múltiplas partes)
                    Multipart multipart = (Multipart) content;
                    for (int i = 0; i < multipart.getCount(); i++) {
                        BodyPart part = multipart.getBodyPart(i);
                        System.out.println("Parte " + i + ": " + part.getContent());
                    }
                } else {
                    System.out.println("Conteúdo desconhecido: " + content);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Erro ao obter o conteúdo da mensagem: " + e.getMessage());
            }
        }

        // Fechando a conexão com a caixa de entrada
        inbox.close(false);
        store.close();
    }
}

