package com.mlorenzo.app.ws.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.mlorenzo.app.ws.io.data.UserEntity;
import com.mlorenzo.app.ws.io.repositories.UserRepository;

import io.jsonwebtoken.Jwts;

public class AuthorizationFilter extends BasicAuthenticationFilter {

	private final UserRepository userRepository;
	
	public AuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
		super(authenticationManager);
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String header = request.getHeader(SecurityConstants.HEADER_STRING);
		
		if(header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			chain.doFilter(request, response);
			return;
		}
		
		UsernamePasswordAuthenticationToken authentication = getAuthentication(header);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(request, response);
	}
	
	private UsernamePasswordAuthenticationToken getAuthentication(String header) {
		String token = header.replace(SecurityConstants.TOKEN_PREFIX, "");
		
		String userEmail = Jwts.parser()
				.setSigningKey(SecurityConstants.getTokenSecret())
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
		
		if(userEmail == null)
			return null;
		
		UserEntity userEntity = userRepository.findByEmail(userEmail);
		
		if(userEntity == null)
			return null;
		
		UserPrincipal userPrincipal = new UserPrincipal(userEntity);
		
		// Se comenta porque ahora tenemos que pasarle también a este constructor una colección de tipo "GrantedAuthority" de Spring Security con los roles y los authorities o permisos del usuario
		//return new UsernamePasswordAuthenticationToken(userEmail, null, new ArrayList<>());
		return new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
	}
	
	
	

}
