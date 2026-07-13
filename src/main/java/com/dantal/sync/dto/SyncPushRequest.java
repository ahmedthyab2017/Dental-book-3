package com.dantal.sync.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SyncPushRequest extends EncryptedEnvelopeDto {

    @NotNull
    private Long baseVersion;
}
