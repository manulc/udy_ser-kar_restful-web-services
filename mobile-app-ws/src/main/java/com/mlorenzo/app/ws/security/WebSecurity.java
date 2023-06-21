package com.mlorenzo.app.ws.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.mlorenzo.app.ws.io.repositories.UserRepository;
import com.mlorenzo.app.ws.services.UserService;

// Esta anotación habilita el uso de anotaciones de seguridad a nivel de métodos
// Si el atributo "securedEnabled" de esta anotación es "true", se habilita el uso de anotación @Secured
// Si el atributo "prePostEnabled" de esta anotación es "true", se habilita el uso de las anotaciones @PreAuthorize y @PostAuthorize(ámbas anotaciones son más avanzadas que la anotación @Secured)
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // Esta anotación puede ponerse en cualquier clase que esté anotada con la anotación Configuration
@EnableWebSecurity // Esta anotación incluye la anotación @Configuration
public class WebSecurity extends WebSecurityConfigurerAdapter {
	private final UserService userService;
	private final PasswordEncoder bCryptPasswordEncoder;
	private final UserRepository userRepository;
	
	public WebSecurity(UserService userService, PasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
		this.userService = userService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userRepository = userRepository;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL, SecurityConstants.PASSWORD_RESET_REQUEST_URL).permitAll()
			.antMatchers(HttpMethod.PATCH, SecurityConstants.VERIFICATION_EMAIL_URL, SecurityConstants.PASSWORD_RESET_URL).permitAll()
			// Cuando usamos el método "hasRole" o "hasAnyRole", no hay que añadir el prefijo "ROLE_" porque ya lo hace Spring Security de foma automática por detrás
			// Cuando usamos el método "hasAuthority" o "hasAnyAuthority" para especificar un role o varios roles, aquí si tenemos que añadir el prefijo "ROLE_"
			// Se comentan estas líneas porque ahora usamos anotaciones de seguridad a nivel de métodos
			//.antMatchers(HttpMethod.DELETE, "/users/*").hasRole("ADMIN")
			//.antMatchers(HttpMethod.DELETE, "/users/*").hasAuthority("ROLE_ADMIN")
			//.antMatchers(HttpMethod.DELETE, "/users/*").hasAuthority("DELETE_AUTHORITY")
			.antMatchers("/v2/api-docs", "/swagger*/**", "/webjars/**").permitAll()
			// Se comenta porque ahora sólo usamos la base de datos embebida o interna H2 en el classpath relativo a los tests
			//.antMatchers(SecurityConstants.H2_CONSOLE_PATH).permitAll()
			.anyRequest().authenticated()
			.and()
			// Registramos nuestro filtro de autenticación de usuarios
			.addFilter(getAuthenticationFilter())
			// Registramos nuestro filtro de autorización de usuarios
			.addFilter(new AuthorizationFilter(authenticationManager(), userRepository))
			// Para devolver el código de erro 401(Unauthorized) en vez de 403(Forbidden) en el proceso de autenticación
			.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
			.and()
			// Se desactiva el manejo de sesión de Spring Security(por defecto activado) porque esta aplicación se trata de una API Rest y no se mantiene el estado de la sesión
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.csrf().disable()
			// Habilita CORS teniendo en cuenta, en nuestro caso, la configuración global implementada en el bean de Spring "corsConfigurationSource" que hay más abajo
			.cors();
			//.and()
			// Para poder visualizar correctamente la consola de la base de datos H2
			// Se comenta porque ahora sólo usamos la base de datos embebida o interna H2 en el classpath relativo a los tests
			//.headers().frameOptions().disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
	}
	
	// Método para establecer nuestra ruta de login personalizada
	private AuthenticationFilter getAuthenticationFilter() throws Exception {
		final AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager());
		authenticationFilter.setFilterProcessesUrl("/users/login");
		return authenticationFilter;
	}
	
	// Nota: Es requerido(u obligatorio) que este bean de Spring se llame "corsConfigurationSource" para que la configuración global de CORS funcione correctamente
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		
		configuration.setAllowedOrigins(List.of("*")); // También podemos especificar los orígenes que se permiten mediante la expresión '"http://localhost:3001', "https://localhost:3002"'
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); // Podemos usar también la expresión "*" para permitir todos los tipos de método http
		configuration.setAllowCredentials(true); // Para permitir que haya credenciales(Cookies, cabeceras de autorización, certificados SSL, etc...) en las peticiones http
		configuration.setAllowedHeaders(Arrays.asList("*")); // También podemos especificar las cabecera que se permiten mediante la expresión '"Authorization", "Cache-Control", "Content-Type"'
		
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		// Aplicamos esta configuración de CORS a cualquier ruta o endpoint de cualquiera de los controladores REST de esta aplicación
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
