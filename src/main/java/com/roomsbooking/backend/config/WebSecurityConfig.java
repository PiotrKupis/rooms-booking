package com.roomsbooking.backend.config;

import com.roomsbooking.backend.security.JwtAuthenticationFilter;
import com.roomsbooking.backend.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Method responsible for registering bean responsible for encrypting passwords.
     *
     * @return bean of type {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Method responsible for exposing AuthenticationManager as a Bean.
     *
     * @return bean of type {@link AuthenticationManager}
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
            .passwordEncoder(passwordEncoder());
    }

    /**
     * Method responsible for configuring web security.
     *
     * @param http object of type {@link HttpSecurity}
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
            .and()
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/auth/register", "/auth/login", "/auth/refresh-token", "/room")
            .permitAll()
            .antMatchers("/auth/logout")
            .hasAnyAuthority("ADMIN", "MODERATOR", "USER")

            .antMatchers("/user", "/user/{email}/{role}")
            .hasAnyAuthority("ADMIN")

            .antMatchers("/resort/{resort-name}")
            .permitAll()
            .antMatchers(HttpMethod.POST, "/resort", "/room")
            .hasAnyAuthority("ADMIN", "MODERATOR")
            .antMatchers("/resort/owner/{email}", "/resort/{resort-name}/rooms")
            .hasAnyAuthority("ADMIN", "MODERATOR")

            .antMatchers(HttpMethod.GET, "/room", "/room/{resort-name}/{room-number}",
                "/room/{resort-name}/{room-number}/photos").permitAll()
            .antMatchers(HttpMethod.POST, "/room/{resort-name}/{room-number}",
                "/room/{resort-name}/{room-number}/photo")
            .hasAnyAuthority("ADMIN", "MODERATOR")
            .antMatchers(HttpMethod.PUT, "/room/{resort-name}/{room-number}")
            .hasAnyAuthority("ADMIN", "MODERATOR")
            .antMatchers(HttpMethod.DELETE, "/room/{resort-name}/{room-number}")
            .hasAnyAuthority("ADMIN", "MODERATOR")

            .antMatchers("/reservation")
            .hasAnyAuthority("ADMIN", "MODERATOR", "USER")

            .antMatchers("/search", "/search/quantity").permitAll()

            .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-docs")
            .permitAll()
            .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
