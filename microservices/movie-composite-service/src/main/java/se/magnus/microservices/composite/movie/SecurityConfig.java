
package se.magnus.microservices.composite.movie;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.http.HttpMethod.*;

@EnableWebFluxSecurity
public class SecurityConfig {

	@Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
		http
			.authorizeExchange()
				.pathMatchers("/actuator/**").permitAll()
				.pathMatchers(POST, "/movie-composite/**").hasAuthority("SCOPE_movie:write")
				.pathMatchers(DELETE, "/movie-composite/**").hasAuthority("SCOPE_movie:write")
				.pathMatchers(GET, "/movie-composite/**").hasAuthority("SCOPE_movie:read")
				.anyExchange().authenticated()
				.and()
			.oauth2ResourceServer()
				.jwt();
		return http.build();
	}
}