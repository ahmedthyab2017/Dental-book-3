package com.dantal.sync.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SyncPullResponse {

    private String ciphertext;
    private String iv;
    private String salt;
    private String alg;
    private Object kdf;
    private long version;
}
