package com.dantal.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("prod")
public class NoOpMailService implements MailService {

    @Override
    public void sendPasswordReset(String email, String token) {
        log.warn("Mail delivery is not configured; password reset token for {} was not sent", email);
    }
}
