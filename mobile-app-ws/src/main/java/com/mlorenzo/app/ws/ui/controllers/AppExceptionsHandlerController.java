package com.mlorenzo.app.ws.ui.controllers;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mlorenzo.app.ws.exceptions.UserServiceException;
import com.mlorenzo.app.ws.ui.models.responses.ErrorMessage;

@ControllerAdvice
public class AppExceptionsHandlerController {

	@ExceptionHandler(UserServiceException.class)
	public ResponseEntity<ErrorMessage> handleUserServiceException(UserServiceException ex) {
		ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
		return new ResponseEntity<>(errorMessage, ex.getStatus());
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorMessage> handleOtherExceptions(Exception ex) {
		ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());
		return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
