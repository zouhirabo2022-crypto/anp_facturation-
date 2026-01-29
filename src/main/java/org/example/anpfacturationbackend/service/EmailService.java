package org.example.anpfacturationbackend.service;

public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);

    void sendMessageWithAttachment(String to, String subject, String text, byte[] attachmentData,
            String attachmentName);
}
