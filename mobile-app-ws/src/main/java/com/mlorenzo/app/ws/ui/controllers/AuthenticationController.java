package com.mlorenzo.app.ws.ui.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mlorenzo.app.ws.ui.models.requests.UserLoginRequestModel;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

// Esta clase es un controlador REST "fake" que sólo se utiliza para documentar en Swagger la operación
// de login implementada por Spring Security

@RestController
public class AuthenticationController {
	
	// Este método nunca debe llamarse ya que se utiliza la implementación de Spring Security
	@ApiOperation("User login")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "Response Headers", responseHeaders = {
                @ResponseHeader(name = "authorization", description = "Bearer --JWT value here--", response = String.class),
                @ResponseHeader(name = "userId", description = "--Public User Id value here--;", response = String.class)
            }),
    		@ApiResponse(code = 401, message = "Invalid Credentials")
    })
	@PostMapping("/users/login")
	public void theFakeLogin(@RequestBody UserLoginRequestModel userLoginRequestModel) {
		throw new IllegalStateException("This method should not be called. This method is implemented by Spring Security");
	}
}
