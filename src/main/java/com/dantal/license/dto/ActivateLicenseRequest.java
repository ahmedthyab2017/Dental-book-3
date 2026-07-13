package com.dantal.license.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivateLicenseRequest {

    @NotBlank
    private String key;
}
