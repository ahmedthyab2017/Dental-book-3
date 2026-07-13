package com.dantal.sync.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EncryptedEnvelopeDto {

    @NotBlank
    private String ciphertext;

    @NotBlank
    private String iv;

    @NotBlank
    private String salt;

    private String alg = "AES-256-GCM";

    private Map<String, Object> kdf;

    private Instant clientUpdatedAt;
}
