package com.dantal.sync.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class BackupResponse {

    private String id;
    private String label;
    private Instant createdAt;
}
