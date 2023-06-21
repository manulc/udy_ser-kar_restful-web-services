package com.mlorenzo.app.ws.services.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mlorenzo.app.ws.exceptions.UserServiceException;
import com.mlorenzo.app.ws.io.data.AddressEntity;
import com.mlorenzo.app.ws.io.data.RoleEntity;
import com.mlorenzo.app.ws.io.data.UserEntity;
import com.mlorenzo.app.ws.io.repositories.RoleRepository;
import com.mlorenzo.app.ws.io.repositories.UserRepository;
import com.mlorenzo.app.ws.shared.EmailSender;
import com.mlorenzo.app.ws.shared.Roles;
import com.mlorenzo.app.ws.shared.Utils;
import com.mlorenzo.app.ws.shared.dtos.AddressDto;
import com.mlorenzo.app.ws.shared.dtos.UserDto;

// Unit Tests

class UserServiceImplTest {
	static final String USER_ID_ADDRESS_ID = "hfg775b";
	static final String ENCRYPTED_PASSWORD = "vvbj67hg";
	static final String PASSWORD = "12345678";
	
	// Esta anotación crea una instancia de "UserServiceImpl" y le inyectar los Mocks
	@InjectMocks
	UserServiceImpl userService;

	@Mock
	UserRepository userRepository;
	
	@Mock
	Utils utils;
	
	@Mock
	PasswordEncoder bCryptPasswordEncoder;
	
	@Mock
	EmailSender emailSender;
	
	@Mock
	RoleRepository roleRepository;
	
	// This will allow us to call all the normal methods of the object while still tracking every interaction, just as we would with a mock.
	// It will still behave in the same way as the normal instance; the only difference is that it will also be instrumented to track all the interactions with it
	// This way is preparing a modelmapper with default constructor
	// Ya que queremos usar el ModelMapper real y no una simulación o Mock de él
	@Spy
	ModelMapper modelMapper;
	
	UserEntity userEntity;
	UserDto userDto;
	
	@BeforeEach
	void setUp() throws Exception {
		// To enable Mockito annotations (such as @Spy, @Mock, … )
		MockitoAnnotations.initMocks(this);
		userEntity = getUserEntity();
		userDto = getUserDto();
	}

	@Test
	void testGetUser() {
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		UserDto userDto = userService.getUser(anyString());
		assertNotNull(userDto);
		assertEquals(userEntity.getFirstName(), userDto.getFirstName());
	}
	
	@Test
	void testGetUser_UsernameNotFoundException() {
		when(userRepository.findByEmail(anyString())).thenReturn(null);
		assertThrows(UsernameNotFoundException.class, () -> userService.getUser(anyString()));
	}

	@Test
	void testCreateUser() {
		when(userRepository.findByEmail(anyString())).thenReturn(null);
		when(utils.generateId(anyInt())).thenReturn(USER_ID_ADDRESS_ID);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(ENCRYPTED_PASSWORD);
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
		doNothing().when(emailSender).sendVerifyEmail(any(UserDto.class));
		when(roleRepository.findByName(anyString())).thenReturn(getRoleEntity());
		
		UserDto storedUserDetails = userService.createUser(userDto);
		
		assertNotNull(storedUserDetails);
		assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());
		assertEquals(userEntity.getLastName(), storedUserDetails.getLastName());
		assertNotNull(storedUserDetails.getUserId());
		assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size());
		// + 1 debido a la generación del id para el usuario 
		verify(utils, times(storedUserDetails.getAddresses().size() + 1)).generateId(30);
		verify(bCryptPasswordEncoder, times(1)).encode(PASSWORD);
		// Si no se indica el método estático "times", por defecto es 1
		verify(userRepository).save(any(UserEntity.class));
		// Además de los Mocks, también podemos interactuar con los Espías(Spies) para verificar datos con Mockito 
		verify(modelMapper, times(2)).map(any(), any());
		verify(emailSender).sendVerifyEmail(any(UserDto.class));
		verify(roleRepository, times(1)).findByName(anyString());
	}
	
	@Test
	void testCreateUser_UserServiceException() {
		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
		assertThrows(UserServiceException.class, () -> userService.createUser(userDto));
	}
	
	private UserEntity getUserEntity() {
		UserEntity userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Manuel");
		userEntity.setLastName("Lorenzo");
		userEntity.setEmail("test@test.com");
		userEntity.setUserId(USER_ID_ADDRESS_ID);
		userEntity.setEncryptedPassword(ENCRYPTED_PASSWORD);
		
		AddressEntity shippingAddressEntity = new AddressEntity();
		shippingAddressEntity.setType("shipping");
		shippingAddressEntity.setCity("Vancouver");
		shippingAddressEntity.setCountry("Canada");
		shippingAddressEntity.setPostalCode("ABC123");
		shippingAddressEntity.setStreetName("123 Street name");
		
		AddressEntity billingAddressEntity = new AddressEntity();
		billingAddressEntity.setType("billing");
		billingAddressEntity.setCity("Vancouver");
		billingAddressEntity.setCountry("Canada");
		billingAddressEntity.setPostalCode("ABC123");
		billingAddressEntity.setStreetName("123 Street name");
		
		List<AddressEntity> addresses = List.of(shippingAddressEntity, billingAddressEntity);
		userEntity.setAddresses(addresses);
		
		return userEntity;
	}
	
	private UserDto getUserDto() {
		UserDto userDto = new UserDto();
		userDto.setId(userEntity.getId());
		userDto.setFirstName(userEntity.getFirstName());
		userDto.setLastName(userEntity.getLastName());
		userDto.setEmail(userEntity.getEmail());
		userDto.setPassword(PASSWORD);
		userDto.setUserId(userEntity.getUserId());
		userDto.setEncryptedPassword(userEntity.getEncryptedPassword());
		
		List<AddressDto> addresses = userEntity.getAddresses().stream()
				.map(addressEntity -> {
					AddressDto addressDto = new AddressDto();
					BeanUtils.copyProperties(addressEntity, addressDto);
					return addressDto;
				})
				.collect(Collectors.toList());
		
		userDto.setAddresses(addresses);
		
		return userDto;
	}
	
	private RoleEntity getRoleEntity() {
		RoleEntity roleEntity = new RoleEntity();
		roleEntity.setId(1L);
		roleEntity.setName(Roles.ROLE_USER.name());
		roleEntity.setUsers(Set.of(userEntity));
		return roleEntity;
	}
}
