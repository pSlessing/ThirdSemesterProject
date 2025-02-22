package com.mailmak.time_registration_system;

import com.mailmak.time_registration_system.security.SecurityConfig;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@Import(TimeRegistrationSystemApplicationTests.TestSecurityConfig.class)
public class TimeRegistrationSystemApplicationTests {

    @Import(SecurityConfig.class)
    @TestConfiguration
    public static class TestSecurityConfig {
        @Bean
        public JwtDecoder jwtDecoder() {
            // Mock JwtDecoder
            return token -> Jwt.withTokenValue(token)
                    .header("alg", "none")
                    .claim("sub", "testuser")
                    .build();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(AbstractHttpConfigurer::disable)
                    .oauth2ResourceServer(oauth2 -> oauth2
                            .jwt(Customizer.withDefaults()))
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }
    }


    @Test
    public void contextLoads() {
        // This is a simple test to check if the context loads successfully
    }
}
