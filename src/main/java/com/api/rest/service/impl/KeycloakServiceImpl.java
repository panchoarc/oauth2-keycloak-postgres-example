package com.api.rest.service.impl;

import com.api.rest.dto.UserDTO;
import com.api.rest.service.IKeycloakService;
import com.api.rest.util.KeycloakProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakServiceImpl implements IKeycloakService {

    private final KeycloakProvider keycloakProvider;


    /**
     * Método para listar todos los usuarios de keycloak
     *
     * @return List<UserRepresentation>
     */
    @Override
    public List<UserRepresentation> findAllUsers() {
        return keycloakProvider.realmResource().users().list();
    }


    /**
     * Método para buscar un usuario por username
     *
     * @return List<UserRepresentation>
     */
    @Override
    public List<UserRepresentation> searchUserByUsername(String username) {
        return keycloakProvider.realmResource().users().searchByUsername(username, true);
    }

    /**
     * Método para crear un usuario en Keycloak
     *
     * @return String
     */

    @Override
    public String createUser(@NonNull UserDTO userDTO) {

        int status;

        UsersResource usersResource = keycloakProvider.usersResource();

        UserRepresentation userRepresentation = toUserRepresentation(userDTO);


        try (Response response = usersResource.create(userRepresentation)) {
            status = response.getStatus();

            if (status == HttpStatus.SC_CREATED) {
                String path = response.getLocation().getPath();
                String userId = path.substring(path.lastIndexOf("/") + 1);

                CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
                credentialRepresentation.setTemporary(false);
                credentialRepresentation.setType(OAuth2Constants.PASSWORD);
                credentialRepresentation.setValue(userDTO.password());
                usersResource.get(userId).resetPassword(credentialRepresentation);
                RealmResource realmResource = keycloakProvider.realmResource();
                List<RoleRepresentation> roleRepresentations;

                if (userDTO.roles() == null || userDTO.roles().isEmpty()) {
                    roleRepresentations = List.of(realmResource.roles().get("user").toRepresentation());

                } else {
                    roleRepresentations = realmResource.roles()
                            .list()
                            .stream()
                            .filter(role -> userDTO.roles()
                                    .stream()
                                    .anyMatch(roleName -> roleName.equalsIgnoreCase(role.getName()))).toList();
                }

                realmResource.users().get(userId).roles().realmLevel().add(roleRepresentations);
                return "User Created Successfully";
            } else if (status == HttpStatus.SC_CONFLICT) {
                log.error("User Exists already");
                return "User exist already";
            } else {
                log.error("Error creating user, please contact with the administrator!!");
                return "Error creating user, please contact with the administrator!!";
            }
        }

    }

    private UserRepresentation toUserRepresentation(@NonNull UserDTO userDTO) {
        UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setFirstName(userDTO.firstName());
        userRepresentation.setLastName(userDTO.lastName());
        userRepresentation.setEmail(userDTO.email());
        userRepresentation.setUsername(userDTO.username());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);
        return userRepresentation;
    }


    /**
     * Método para borrar un usuario en Keycloak
     *
     */

    @Override
    public void deleteUser(String userId) {
        keycloakProvider.usersResource().get(userId).remove();
    }

    /**
     * Método para actualizar un usuario en keycloak
     *
     */
    @Override
    public void updateUser(String userId, @NonNull UserDTO userDTO) {

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(OAuth2Constants.PASSWORD);
        credentialRepresentation.setValue(userDTO.password());


        UserRepresentation userRepresentation = toUserRepresentation(userDTO);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

        UserResource userResource = keycloakProvider.usersResource().get(userId);
        userResource.update(userRepresentation);


    }
}
