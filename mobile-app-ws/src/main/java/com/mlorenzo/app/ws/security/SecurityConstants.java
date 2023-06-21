package com.mlorenzo.app.ws.security;

import org.springframework.core.env.Environment;

import com.mlorenzo.app.ws.SpringApplicationContext;

public class SecurityConstants {
	public static final long EXPIRATION_TIME = 864000000; // 10 days
	public static final long PASSWORD_RESET_EXPIRATION_TIME = 3600000; // 1 hour
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SIGN_UP_URL = "/users";
	public static final String VERIFICATION_EMAIL_URL = "/users/email-verification";
	public static final String PASSWORD_RESET_REQUEST_URL = "/users/password-reset-request";
	public static final String PASSWORD_RESET_URL = "/users/password-reset";
	public static final String H2_CONSOLE_PATH = "/h2-console/**";
	
	// Tenemos que obtener de esta forma el valor de la clave para la firma y validación de los token's JWT
	// desde el archivo de propiedades porque esta clase no es un componente de Spring y, por lo tanto, no
	// podemos usar la inyección directa de Spring.
	public static String getTokenSecret() {
		Environment env = (Environment)SpringApplicationContext.getBean("environment");
		return env.getProperty("tokenSecret");
	}
}
