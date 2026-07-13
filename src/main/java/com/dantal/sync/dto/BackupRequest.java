package com.dantal.sync.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BackupRequest extends EncryptedEnvelopeDto {

    private String label;
}
