package com.mlorenzo.app.ws.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// Integration Tests

//@RunWith(SpringRunner.class) // Para Junit 4 - Carga el contexto de Spring
@ExtendWith(SpringExtension.class) // Para Junit 5 - Carga el contexto de Spring
// Esta es la clave que se usó para firmar el token JWT de la prueba del método "testHasTokenExpired".
// Por lo tanto, es necesaria para validar dicho token y verificar si ha expirado.
@SpringBootTest(properties = { "tokenSecret: jf9i4jgu83nfl0jfu57ejf7", "spring.datasource.url=jdbc:h2:mem:testdb" })
class UtilsTest {

	@Autowired
	Utils utils;

	@Test
	void testGenerateId() {
		String userId1 = utils.generateId(30);
		String userId2 = utils.generateId(30);
		
		assertNotNull(userId1);
		assertTrue(userId1.length() == 30);
		assertNotNull(userId2);
		assertTrue(!userId1.equalsIgnoreCase(userId2));
	}
	
	@Test
	void testHasTokenNotExpired() {
		String token = utils.generateEmailVerificationToken("abc123");
		assertNotNull(token);
		assertFalse(utils.hasTokenExpired(token));
	}
	
	@Test
	void testHasTokenExpired() {
		String expiredToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MUB0ZXN0LmNvbSIsImV4cCI6MTUzMjc3Nzc3NX0.cdudUo3pwZLN9UiTuXiT7itpaQs6BgUPU0yWbNcz56-l1Z0476N3H_qSEHXQI5lUfaK2ePtTWJfROmf0213UJA";
		assertTrue(utils.hasTokenExpired(expiredToken));
	}

}
