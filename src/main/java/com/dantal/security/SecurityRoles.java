package com.dantal.security;

public final class SecurityRoles {

    public static final String CLINICAL = "hasAnyRole('SUPER_ADMIN','ADMIN','DOCTOR','DENTIST','RECEPTIONIST','ASSISTANT','ACCOUNTANT')";
    public static final String PLATFORM = "hasRole('SUPER_ADMIN')";

    private SecurityRoles() {
    }
}
