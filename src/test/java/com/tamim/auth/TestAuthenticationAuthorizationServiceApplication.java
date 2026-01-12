package com.tamim.auth;

import org.springframework.boot.SpringApplication;

public class TestAuthenticationAuthorizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(AuthServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
