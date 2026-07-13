package com.dantal.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Clinic owner registration — matches frontend {@code cloud.ts} contract:
 * {@code { clinicName, email, password, deviceName }}
 */
@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    @Size(max = 255)
    private String clinicName;

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Pattern(regexp = "^\\+?[0-9\\-\\s()]{7,20}$", message = "Invalid phone number format")
    private String phone;

    @Size(max = 255)
    private String deviceId;

    @Size(max = 512)
    private String deviceName;
}
