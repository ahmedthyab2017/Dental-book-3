package com.dantal.common.security;

import com.dantal.common.exception.ForbiddenException;
import com.dantal.security.UserPrincipal;

import java.util.UUID;

public final class ClinicContext {

    private ClinicContext() {
    }

    public static UUID requireClinicId(UserPrincipal principal) {
        if (principal.getClinicId() == null) {
            throw new ForbiddenException("User is not associated with a clinic");
        }
        return principal.getClinicId();
    }
}
