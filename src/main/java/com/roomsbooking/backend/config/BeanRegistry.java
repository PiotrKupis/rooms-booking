package com.roomsbooking.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Class responsible for registering beans.
 */
@Configuration
public class BeanRegistry {

    /**
     * Method responsible for registering bean responsible for encrypting passwords.
     *
     * @return bean of type {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
