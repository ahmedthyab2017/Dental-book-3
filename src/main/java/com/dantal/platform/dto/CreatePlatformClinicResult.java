package com.dantal.platform.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePlatformClinicResult {

    private PlatformClinicResponse clinic;
    private String managerEmail;
    private String managerPassword;
    private boolean licenseActivated;
}
