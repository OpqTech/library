package opqlibrary;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService userDetailsService) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(
                    "/login",
                    "/register",
                    "/api/users/register",
                    "/password-reset-request",
                    "/password-reset",
                    "/api/password-reset/request",
                    "/api/password-reset/confirm"
                ).permitAll()
                .requestMatchers("/api/books/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/", "/markRead/**", "/markProgress/**", "/unmarkRead/**", "/unmarkProgress/**", "/delete/**", "/borrow", "/return/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .rememberMe(rm -> rm
                .key("opq-remember-me-key")
                .tokenValiditySeconds(1209600) // 14 days
            )
            .httpBasic();
        return http.build();
    }
} 