package com.dantal.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile({"dev", "test"})
public class LoggingMailService implements MailService {

    @Override
    public void sendPasswordReset(String email, String token) {
        log.info("Password reset email for {} — token: {}", email, token);
    }
}
