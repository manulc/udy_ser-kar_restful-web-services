package com.mlorenzo.app.ws.ui.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;

import com.mlorenzo.app.ws.services.UserService;
import com.mlorenzo.app.ws.shared.dtos.AddressDto;
import com.mlorenzo.app.ws.shared.dtos.UserDto;
import com.mlorenzo.app.ws.ui.models.responses.UserRest;

// Unit Tests

class UserControllerTest {
	
	// Esta anotación crea una instancia de "UserController" y le inyectar los Mocks
	@InjectMocks
	UserController userController;
	
	@Mock
	UserService userService;
	
	// This will allow us to call all the normal methods of the object while still tracking every interaction, just as we would with a mock.
	// It will still behave in the same way as the normal instance; the only difference is that it will also be instrumented to track all the interactions with it
	// This way is preparing a modelmapper with default constructor
	// Ya que queremos usar el ModelMapper real y no una simulación o Mock de él
	@Spy
	ModelMapper modelMapper;

	@BeforeEach
	void setUp() throws Exception {
		// To enable Mockito annotations (such as @Spy, @Mock, … )
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testGetUser() {
		UserDto userDto = getUserDto();
		
		when(userService.getUserByUserId(anyString())).thenReturn(userDto);
		
		UserRest returnedUserRest = userController.getUser(anyString());
		
		assertNotNull(returnedUserRest);
		assertEquals(userDto.getUserId(), returnedUserRest.getUserId());
		assertEquals(userDto.getFirstName(), returnedUserRest.getFirstName());
		assertEquals(userDto.getLastName(), returnedUserRest.getLastName());
		assertTrue(userDto.getAddresses().size() == returnedUserRest.getAddresses().size());
		// Además de los Mocks, también podemos interactuar con los Espías(Spies) para verificar datos con Mockito 
		// Si no indicamos el método estático "times", por defecto es 1 vez
		verify(modelMapper).map(any(), any());
	}
	
	private UserDto getUserDto() {
		UserDto userDto = new UserDto();
		userDto.setFirstName("Manuel");
		userDto.setLastName("Lorenzo");
		userDto.setEmail("test@test.com");
		userDto.setUserId("fnng67u");
		userDto.setEncryptedPassword("xxdbf556h");
		
		AddressDto shippingAddressDto = new AddressDto();
		shippingAddressDto.setType("shipping");
		shippingAddressDto.setCity("Vancouver");
		shippingAddressDto.setCountry("Canada");
		shippingAddressDto.setPostalCode("ABC123");
		shippingAddressDto.setStreetName("123 Street name");
		
		AddressDto billingAddressDto = new AddressDto();
		billingAddressDto.setType("billing");
		billingAddressDto.setCity("Vancouver");
		billingAddressDto.setCountry("Canada");
		billingAddressDto.setPostalCode("ABC123");
		billingAddressDto.setStreetName("123 Street name");
		
		userDto.setAddresses(Arrays.asList(shippingAddressDto, billingAddressDto));
		
		return userDto;
	}
}
