package com.api.rest.enums;

import lombok.Getter;

@Getter
public enum KeycloakRoles {
    ADMIN_ROLE("admin_client_role"),
    USER_ROLE("user_client_role");

    public final String role;

    KeycloakRoles(String role) {
        this.role = role;
    }

}
