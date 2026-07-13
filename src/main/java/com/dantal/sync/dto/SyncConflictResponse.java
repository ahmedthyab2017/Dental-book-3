package com.dantal.sync.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SyncConflictResponse {

    private long serverVersion;
    private long clientBaseVersion;
    private String message;
}
