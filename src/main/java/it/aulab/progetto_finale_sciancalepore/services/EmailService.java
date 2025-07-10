package it.aulab.progetto_finale_sciancalepore.services;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String text);
}
