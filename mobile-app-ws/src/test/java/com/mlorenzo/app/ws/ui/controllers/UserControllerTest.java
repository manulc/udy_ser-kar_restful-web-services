package com.mlorenzo.app.ws.ui.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mlorenzo.app.ws.services.UserService;
import com.mlorenzo.app.ws.ui.models.responses.AddressRest;
import com.mlorenzo.app.ws.ui.models.responses.UserRest;

// Unit Tests

class UserControllerTest {
	
	// Esta anotación crea una instancia de "UserController" y le inyectar los Mocks
	@InjectMocks
	UserController userController;
	
	@Mock
	UserService userService;

	@BeforeEach
	void setUp() throws Exception {
		// To enable Mockito annotations (such as @Spy, @Mock, … )
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testGetUser() {
		UserRest userRest = getUserRest();
		when(userService.getUserByUserId(anyString())).thenReturn(userRest);
		UserRest returnedUserRest = userController.getUser(anyString());
		assertNotNull(returnedUserRest);
		assertEquals(userRest.getUserId(), returnedUserRest.getUserId());
		assertEquals(userRest.getFirstName(), returnedUserRest.getFirstName());
		assertEquals(userRest.getLastName(), returnedUserRest.getLastName());
		assertTrue(userRest.getAddresses().size() == returnedUserRest.getAddresses().size());
	}
	
	private UserRest getUserRest() {
		UserRest userRest = new UserRest();
		userRest.setFirstName("Manuel");
		userRest.setLastName("Lorenzo");
		userRest.setEmail("test@test.com");
		userRest.setUserId("fnng67u");
		AddressRest shippingAddressRest = new AddressRest();
		shippingAddressRest.setType("shipping");
		shippingAddressRest.setCity("Vancouver");
		shippingAddressRest.setCountry("Canada");
		shippingAddressRest.setPostalCode("ABC123");
		shippingAddressRest.setStreetName("123 Street name");
		AddressRest billingAddressRest = new AddressRest();
		billingAddressRest.setType("billing");
		billingAddressRest.setCity("Vancouver");
		billingAddressRest.setCountry("Canada");
		billingAddressRest.setPostalCode("ABC123");
		billingAddressRest.setStreetName("123 Street name");
		userRest.setAddresses(Arrays.asList(shippingAddressRest, billingAddressRest));
		return userRest;
	}
}
