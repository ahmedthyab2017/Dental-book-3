package com.dantal.mail;

public interface MailService {

    void sendPasswordReset(String email, String token);
}
