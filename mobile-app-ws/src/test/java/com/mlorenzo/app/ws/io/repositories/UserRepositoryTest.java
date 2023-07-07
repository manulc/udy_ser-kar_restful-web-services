package com.mlorenzo.app.ws.io.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mlorenzo.app.ws.io.data.AddressEntity;
import com.mlorenzo.app.ws.io.data.UserEntity;

// Integration Tests

@ExtendWith(SpringExtension.class)
// Anotación para levantar sólo la capa de persistencia, ésto es, los repositorios y las clases que son entidades.
// Esta anotación también incluye la anotación @Transactional y, por defecto, por cada test, al finalizar su ejecución se realiza un rollback.
// También, con esta anotación, si en el classpath relativo a los tests se encuentra la dependencia de alguna base de datos embebida, como por
// ejemplo la H2, se utiliza como base de datos por defecto para las pruebas esa base de datos embebida o interna.
@DataJpaTest
class UserRepositoryTest {

	@Autowired
	UserRepository userRepository;
	
	@PersistenceContext
	EntityManager entityManager;
	
	@BeforeEach
	void setUp() throws Exception {
		// Prepare User Entity
		UserEntity userEntity = new UserEntity();
		userEntity.setFirstName("Manuel");
		userEntity.setLastName("Lorenzo");
		userEntity.setUserId("11f5hh7");
		userEntity.setEncryptedPassword("abc556h7");
		userEntity.setEmail("test@test.com");
		userEntity.setEmailVerificationStatus(true);
		// Prepare User Addresses
		AddressEntity addresEntity = new AddressEntity();
		addresEntity.setType("shipping");
		addresEntity.setAddressId("45gfvv7");
		addresEntity.setCity("Vancouver");
		addresEntity.setCountry("Canada");
		addresEntity.setPostalCode("ABC123");
		addresEntity.setStreetName("123 Street name");
		List<AddressEntity> addresses = List.of(addresEntity);
		userEntity.setAddresses(addresses);
		userRepository.save(userEntity);
		// Prepare User Entity
		UserEntity userEntity2 = new UserEntity();
		userEntity2.setFirstName("Manuel");
		userEntity2.setLastName("Lorenzo");
		userEntity2.setUserId("11f5hh8");
		userEntity2.setEncryptedPassword("abc556h7");
		userEntity2.setEmail("test2@test.com");
		userEntity2.setEmailVerificationStatus(true);
		// Prepare User Addresses
		AddressEntity addresEntity2 = new AddressEntity();
		addresEntity2.setType("shipping");
		addresEntity2.setAddressId("45gfvv8");
		addresEntity2.setCity("Vancouver");
		addresEntity2.setCountry("Canada");
		addresEntity2.setPostalCode("ABC123");
		addresEntity2.setStreetName("123 Street name");
		List<AddressEntity> addresses2 = List.of(addresEntity2);
		userEntity2.setAddresses(addresses2);
		userRepository.save(userEntity2);
	}

	@Test
	void testGetVerifiedUsers() {
		Pageable pageableRequest = PageRequest.of(1, 1);
		Page<UserEntity> pages = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);
		assertNotNull(pages);
		List<UserEntity> userEntities = pages.getContent();
		assertNotNull(userEntities);
		assertTrue(userEntities.size() == 1);
	}
	
	@Test
	void testFindUsersByFirstName() {
		final String firstName = "Manuel";
		List<UserEntity> userEntities = userRepository.findUsersByFirstName(firstName);
		assertNotNull(userEntities);
		assertTrue(userEntities.size() == 2);
		UserEntity userEntity = userEntities.get(0);
		assertEquals(firstName, userEntity.getFirstName());
	}
	
	@Test
	void testFindUsersByLastName() {
		final String lastName = "Lorenzo";
		List<UserEntity> userEntities = userRepository.findUsersByLastName(lastName);
		assertNotNull(userEntities);
		assertTrue(userEntities.size() == 2);
		UserEntity userEntity = userEntities.get(0);
		assertEquals(lastName, userEntity.getLastName());
	}
	
	@Test
	void testFindUsersByKeyboard() {
		final String keyboard = "anu";
		List<UserEntity> userEntities = userRepository.findUsersByKeyboard(keyboard);
		assertNotNull(userEntities);
		assertTrue(userEntities.size() == 2);
		UserEntity userEntity = userEntities.get(0);
		assertTrue(userEntity.getLastName().contains(keyboard) || userEntity.getFirstName().contains(keyboard));
	}
	
	@Test
	void testFindUsersFirstNameAndLastNameByKeyboard() {
		final String keyboard = "anu";
		List<Object[]> users = userRepository.findUsersFirstNameAndLastNameByKeyboard(keyboard);
		assertNotNull(users);
		assertTrue(users.size() == 2);
		Object[] user = users.get(0);
		assertTrue(user.length == 2);
		String userFirstName = (String)user[0];
		String userLastName = (String)user[1];
		assertNotNull(userFirstName);
		assertNotNull(userLastName);
		System.out.println("First Name: " + userFirstName);
		System.out.println("Last Name: " + userLastName);
	}
	
	@Test
	void testUpdateUserEmailVerificationStatus() {
		final boolean newEmailVerificationStatus = false;
		final String userId = "11f5hh7";
		userRepository.updateUserEmailVerificationStatus(newEmailVerificationStatus, userId);
		// clear the cache and force userRepository.findByUserId read from database
		entityManager.clear();
		UserEntity userEntity = userRepository.findByUserId(userId);
		assertNotNull(userEntity);
		final boolean storedEmailVerificationStatus = userEntity.getEmailVerificationStatus();
		assertEquals(newEmailVerificationStatus, storedEmailVerificationStatus);
	}
	
	@Test
	void testFindUserEntityByUserId() {
		final String userId = "11f5hh7";
		UserEntity userEntity = userRepository.findUserEntityByUserId(userId);
		assertNotNull(userEntity);
		assertEquals(userId, userEntity.getUserId());
	}
	
	@Test
	void testGetUserFullNameByUserId() {
		final String userId = "11f5hh7";
		List<Object[]> users = userRepository.getUserFullNameByUserId(userId);
		assertNotNull(users);
		assertTrue(users.size() == 1);
		Object[] user = users.get(0);
		assertTrue(user.length == 2);
		final String userFirstName = (String)user[0];
		final String userLastName = (String)user[0];
		assertNotNull(userFirstName);
		assertNotNull(userLastName);
	}
	
	@Test
	void testUpdateUserEntityEmailVerificationStatus() {
		final boolean newEmailVerificationStatus = false;
		final String userId = "11f5hh7";
		userRepository.updateUserEntityEmailVerificationStatus(newEmailVerificationStatus, userId);
		// clear the cache and force userRepository.findByUserId read from database
		entityManager.clear();
		UserEntity userEntity = userRepository.findByUserId(userId);
		assertNotNull(userEntity);
		final boolean storedEmailVerificationStatus = userEntity.getEmailVerificationStatus();
		assertEquals(newEmailVerificationStatus, storedEmailVerificationStatus);
	}
}
