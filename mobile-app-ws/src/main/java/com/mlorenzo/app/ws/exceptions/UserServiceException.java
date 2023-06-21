package com.mlorenzo.app.ws.exceptions;

import org.springframework.http.HttpStatus;

public class UserServiceException extends RuntimeException {

	private static final long serialVersionUID = -4773558597647569060L;
	
	private final HttpStatus status;
	
	public UserServiceException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
	
	public HttpStatus getStatus() {
		return status;
	}

}
