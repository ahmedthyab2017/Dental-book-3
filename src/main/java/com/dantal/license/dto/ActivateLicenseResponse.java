package com.dantal.license.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActivateLicenseResponse {

    private String message;
    private String tier;
}
