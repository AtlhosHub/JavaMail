package com.teste.javamail;

import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.FromTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;
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
        inbox.open(Folder.READ_ONLY);

        // Lista de remetentes permitidos
        List<String> remetentesPermitidos = List.of(
             //   "extremopanda@gmail.com"
                "cauagouveanascimento@gmail.com"
        );

        SearchTerm[] termos = remetentesPermitidos.stream()
                .map(email -> {
                    try {
                        return new FromTerm(new InternetAddress(email));
                    } catch (AddressException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toArray(SearchTerm[]::new);

        SearchTerm filtro = termos.length == 1 ? termos[0] : new OrTerm(termos);


        // Buscar mensagens filtradas
        Message[] messages = inbox.search(filtro);
        System.out.println("Total de mensagens dos remetentes permitidos: " + messages.length);

        for (Message message : messages) {
            System.out.println("Assunto: " + message.getSubject());
            System.out.println("De: " + message.getFrom()[0]);
            System.out.println("Data: " + message.getSentDate());

            try {
                Object content = message.getContent();
                if (content instanceof Multipart) {
                    Multipart multipart = (Multipart) content;
                    for (int i = 0; i < multipart.getCount(); i++) {
                        BodyPart part = multipart.getBodyPart(i);
                        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) ||
                                Part.INLINE.equalsIgnoreCase(part.getDisposition())) {
                            saveAttachmentToDisk(part, message);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Erro ao obter o conteúdo da mensagem: " + e.getMessage());
            }
        }

        inbox.close(false);
        store.close();
    }


    private void saveAttachmentToDisk(BodyPart part, Message message) throws IOException, MessagingException {
        Address[] froms = message.getFrom();
        String remetente = froms.length > 0 ? froms[0].toString() : "desconhecido";

        // Limpa o remetente para nome de arquivo
        remetente = remetente.replaceAll("[^a-zA-Z0-9._-]", "_");

        // Formata a data
        String dataFormatada = new SimpleDateFormat("yyyyMMdd_HHmmss").format(message.getSentDate());

        // Pega a extensão do arquivo original
        String extensao = getFileExtension(part.getFileName());

        // Cria diretório se não existir
        Path attachmentDir = Paths.get("anexos");
        if (!Files.exists(attachmentDir)) {
            Files.createDirectories(attachmentDir);
        }

        // Monta caminho final do arquivo
        String nomeArquivo = remetente + "_" + dataFormatada + extensao;
        Path filePath = attachmentDir.resolve(nomeArquivo);

        try (InputStream inputStream = part.getInputStream();
             OutputStream outputStream = new FileOutputStream(filePath.toFile())) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            System.out.println("✅ Anexo salvo: " + filePath.toAbsolutePath());

        } catch (Exception e) {
            System.out.println("❌ Erro ao salvar anexo: " + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int dotIndex = fileName.lastIndexOf(".");
        return (dotIndex >= 0) ? fileName.substring(dotIndex) : "";
    }


}

