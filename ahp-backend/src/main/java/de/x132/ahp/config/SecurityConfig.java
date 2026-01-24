package de.x132.ahp.config;

import de.x132.ahp.service.AuthenticationService;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
      webSecurityCustomizer() {
    return (web) ->
        web.ignoring()
            .requestMatchers(
                new AntPathRequestMatcher("/**/*.js"),
                new AntPathRequestMatcher("/**/*.css"),
                new AntPathRequestMatcher("/**/*.png"),
                new AntPathRequestMatcher("/**/*.svg"),
                new AntPathRequestMatcher("/**/*.ico"),
                new AntPathRequestMatcher("/**/*.json"),
                new AntPathRequestMatcher("/assets/**"),
                new AntPathRequestMatcher("/index.html"),
                new AntPathRequestMatcher("/"));
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter(
      AuthenticationService authenticationService) {
    return new JwtAuthenticationFilter(authenticationService);
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
        Arrays.asList(
            "http://localhost:4200",
            "http://localhost:3000",
            "https://ahp-backend-lokimax.fly.dev"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        new AntPathRequestMatcher("/assets/**"),
                        // Public API endpoints
                        new AntPathRequestMatcher("/api/public/**"),
                        new AntPathRequestMatcher("/api/clients/register"),
                        new AntPathRequestMatcher("/api/clients/activate"),
                        new AntPathRequestMatcher("/api/clients/login"),
                        new AntPathRequestMatcher("/api/clients/request-password-reset"),
                        new AntPathRequestMatcher("/api/clients/reset-password"),
                        // H2 console
                        new AntPathRequestMatcher("/h2-console/**"))
                    .permitAll()
                    // All other API endpoints require authentication
                    .requestMatchers(new AntPathRequestMatcher("/api/**"))
                    .authenticated()
                    // Non-API routes (Angular SPA deep links) are public
                    .anyRequest()
                    .permitAll())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
