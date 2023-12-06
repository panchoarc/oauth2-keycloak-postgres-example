package com.api.rest.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("admin")
    @PreAuthorize("hasRole('admin_client_role')")
    public String helloAdmin() {
        return "Hello Spring boot with Keycloak -> Admin";
    }


    @GetMapping("user")
    @PreAuthorize("hasRole('admin_client_role') or hasRole('user_client_role')")
    public String helloUser() {
        return "Hello Spring boot with Keycloak -> USER";
    }

}
