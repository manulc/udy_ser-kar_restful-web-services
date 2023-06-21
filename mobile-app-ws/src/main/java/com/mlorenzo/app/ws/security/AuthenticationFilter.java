package com.mlorenzo.app.ws.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlorenzo.app.ws.SpringApplicationContext;
import com.mlorenzo.app.ws.services.UserService;
import com.mlorenzo.app.ws.shared.dtos.UserDto;
import com.mlorenzo.app.ws.ui.models.requests.UserLoginRequestModel;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

// Nota: Por defecto, Spring Security establece la ruta de autenticación en "/login"

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;
	
	public AuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	// Este método se ejecutará cada vez que se envíe una petición http de tipo POST a la ruta de autenticación(Por defecto, "/login")
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		try {
			UserLoginRequestModel creds = new ObjectMapper().readValue(request.getInputStream(), UserLoginRequestModel.class);
			
			// Aquí se ejecutará por detrás nuestra implementación del método "loadUserByUsername" de Spring Security que se encuentra en la clase UserServiceImpl
			// Dicha implementación localizará los datos del usuario en la base de datos, a partir del email y la password que se pasan aquí, y si se localiza, la autenticación es correcta
			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>()));
		}
		catch(IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	// Si la autenticación es correcta, se ejecutará este método que creará el token JWT y lo enviará en la respuesta http
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		// En nuestro caso, Principal es el email del usuario
		String username = ((UserPrincipal)authResult.getPrincipal()).getUsername();

		String token = Jwts.builder()
				.setSubject(username) // En nuestro caso es el email
				.setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
				.compact();
		
		// Tenemos que obtener de esta forma el bean de Spring "userServiceImpl" porque esta clase no es un
		// componente de Spring y, por lo tanto, no podemos usar la inyección directa de Spring.
		UserService userService = (UserService)SpringApplicationContext.getBean("userServiceImpl");
		
		UserDto userDto = userService.getUser(username); // username es el email del usuario
		
		response.addHeader(SecurityConstants.HEADER_STRING, String.format("%s%s", SecurityConstants.TOKEN_PREFIX,token));
		response.addHeader("UserId", userDto.getUserId());
	}
	
	
	
	
}
