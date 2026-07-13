package com.dantal.platform.dto;

import com.dantal.clinic.entity.Clinic;
import com.dantal.license.entity.ClinicLicense;
import com.dantal.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PlatformClinicDetailResponse {

    private PlatformClinicResponse clinic;
    private List<PlatformUserResponse> users;

    public static PlatformClinicDetailResponse from(Clinic clinic, String managerEmail, ClinicLicense license, List<User> users) {
        return PlatformClinicDetailResponse.builder()
                .clinic(PlatformClinicResponse.from(clinic, managerEmail, license))
                .users(users.stream().map(PlatformUserResponse::from).toList())
                .build();
    }
}
