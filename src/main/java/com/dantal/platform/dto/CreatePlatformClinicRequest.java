package com.dantal.platform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePlatformClinicRequest {

    @NotBlank
    @Size(max = 255)
    private String clinicName;

    @NotBlank
    @Email
    @Size(max = 255)
    private String managerEmail;

    @NotBlank
    @Size(min = 8, max = 100)
    private String managerPassword;

    @Size(max = 100)
    private String managerFirstName;

    @Size(max = 100)
    private String managerLastName;

    @Pattern(regexp = "^\\+?[0-9\\-\\s()]{7,20}$", message = "Invalid phone number format")
    private String managerPhone;

    @Size(max = 128)
    private String licenseKey;
}
