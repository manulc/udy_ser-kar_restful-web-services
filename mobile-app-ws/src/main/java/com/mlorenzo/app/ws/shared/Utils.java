package com.mlorenzo.app.ws.shared;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.mlorenzo.app.ws.security.SecurityConstants;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class Utils {
	private static final Random RANDOM = new SecureRandom();
	private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcedefghijklmnopqrstuvwxyz";
	
	public String generateId(int length) {
		return generateRandomString(length);
	}
	
	public boolean hasTokenExpired(String token) {
		try {
			Jwts.parser()
				.setSigningKey(SecurityConstants.getTokenSecret())
				.parseClaimsJws(token);
		}
		catch(ExpiredJwtException ex) {
			return true;
		}
		return false;
	}
	
	public String generateEmailVerificationToken(String userId) {
		return generateToken(userId, SecurityConstants.EXPIRATION_TIME);
	}
	
	public String generatePasswordResetToken(String userId) {
		return generateToken(userId, SecurityConstants.PASSWORD_RESET_EXPIRATION_TIME);
	}
	
	private String generateRandomString(int length) {
		StringBuilder returnValue = new StringBuilder(length);
		for(int i = 0; i < length; i++)
			returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		return returnValue.toString();
	}
	
	private String generateToken(String userId, long expirationTime) {
		return Jwts.builder()
				.setSubject(userId)
				.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
				.signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret())
				.compact();
	}
}
