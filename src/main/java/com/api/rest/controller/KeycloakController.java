package com.api.rest.controller;

import com.api.rest.dto.UserDTO;
import com.api.rest.service.IKeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@PreAuthorize("hasRole('admin_client_role')")
@RequestMapping("/keycloak/user")
@RequiredArgsConstructor
public class KeycloakController {

    private final IKeycloakService keycloakService;


    @GetMapping("/search")
    public ResponseEntity<?> findAllUsers() {
        return ResponseEntity.ok(keycloakService.findAllUsers());

    }


    @GetMapping("/search/{username}")
    public ResponseEntity<?> searchUserByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(keycloakService.searchUserByUsername(username));

    }


    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) throws URISyntaxException {

        String response = keycloakService.createUser(userDTO);
        return ResponseEntity.created(new URI("/keycloak/user/create")).body(response);

    }


    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable("userId") String userId, @RequestBody UserDTO userDTO) {

        keycloakService.updateUser(userId, userDTO);

        return ResponseEntity.ok("User updated Successfully!!");
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable("userId") String userId) {
        keycloakService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }


}
