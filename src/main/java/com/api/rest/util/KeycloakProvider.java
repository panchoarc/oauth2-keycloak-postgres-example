package com.api.rest.util;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class KeycloakProvider {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm.name}")
    private String realmName;

    @Value("${keycloak.realm.master-realm}")
    private String realmMaster;

    @Value("${keycloak.admin.cli}")
    private String adminCli;

    @Value("${keycloak.admin.user-console}")
    private String userConsole;

    @Value("${keycloak.admin.password-console}")
    private String passwordConsole;

    @Value("${keycloak.admin.secret}")
    private String clientSecret;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realmMaster)
                .clientId(adminCli)
                .username(userConsole)
                .password(passwordConsole)
                .clientSecret(clientSecret)
                .resteasyClient(new ResteasyClientBuilderImpl()
                        .connectionPoolSize(10)
                        .build())
                .build();
    }

    @Bean
    public RealmResource realmResource() {
        return keycloak().realm(realmName);
    }

    @Bean
    public UsersResource usersResource() {
        return realmResource().users();
    }
}
