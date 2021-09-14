package com.roomsbooking.backend.config;

import com.roomsbooking.backend.service.UserDetailsServiceImplementation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImplementation userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Method responsible for exposing AuthenticationManager as a Bean.
     *
     * @return object of type {@link AuthenticationManager}
     */
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Method responsible for specifying the AuthenticationManager.
     *
     * @param auth object of type {@link AuthenticationManagerBuilder}
     */
    @Autowired
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
            .and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .exceptionHandling().disable()
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .authorizeRequests()
            .antMatchers("/auth/register", "/auth/login").permitAll()
            .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-docs")
            .permitAll()
            .anyRequest().authenticated();
    }
}
