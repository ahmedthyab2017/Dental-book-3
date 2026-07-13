package com.dantal.auth.dto;

import com.dantal.user.entity.RoleName;
import com.dantal.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
public class UserResponse {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private UUID clinicId;
    private boolean active;
    private boolean emailVerified;
    private Set<RoleName> roles;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .clinicId(user.getClinic() != null ? user.getClinic().getId() : null)
                .active(user.isActive())
                .emailVerified(user.isEmailVerified())
                .roles(user.getRoles().stream()
                        .map(role -> role.getName())
                        .collect(Collectors.toSet()))
                .build();
    }
}
