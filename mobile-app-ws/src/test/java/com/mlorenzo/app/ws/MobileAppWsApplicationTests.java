package com.mlorenzo.app.ws;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

import io.restassured.RestAssured;
import io.restassured.response.Response;

// API Automation Testing with Rest Assured(Integration Tests)

//Anotación para ejecutar los tests en orden en relación al nombre de sus métodos de forma ascendente
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@ActiveProfiles("test")
//@RunWith(SpringRunner.class) // Para Junit 4 - Carga el contexto de Spring
@ExtendWith(SpringExtension.class) // Para Junit 5 - Carga el contexto de Spring
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MobileAppWsApplicationTests {
	static final String BASE_URI = "http://localhost";
	static final int PORT = 8888;
	static final String CONTEXT_PATH = "/mobile-app-ws";
	static final String JSON_CONTENT_TYPE = "application/json";
	static final String JSON_ACCEPT = "application/json";
	static final String USER_EMAIL = "test@test.com";
	static final String USER_PASSWORD = "123";
	static final String AUTHORIZATION_HEADER_NAME = "Authorization";
	static final String VERIFY_EMAIL_TOKEN_PARAM= "token=";
	static String authorizationHeader;
	static String userId;
	static List<Map<String, String>> addresses;
	static String verifyEmailToken;
	
	@RegisterExtension
	static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
		.withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "test"))
		.withPerMethodLifecycle(false);
	
	@BeforeEach
	void setUp(@LocalServerPort int port) throws Exception {
		RestAssured.baseURI = BASE_URI;
		RestAssured.port = port;
	}
	
	// Nota: Usamos A,B,C,... en los nombres de los métodos de los tests para que se ejecuten en el orden deseado en función a la anotación @TestMethodOrder(MethodOrderer.Alphanumeric.class) establecida a nivel de clase
	
	// Create User Test
	@Test
	void testA() {
		// Body de la petición http
		List<Map<String, Object>> userAddresses = new ArrayList<>();
		
		Map<String, Object> shippingAddress = new HashMap<>();
		shippingAddress.put("city", "Vancouver");
		shippingAddress.put("country", "Canada");
		shippingAddress.put("streetName", "123 Street name");
		shippingAddress.put("postalCode", "ABC123");
		shippingAddress.put("type", "shipping");
		
		Map<String, Object> billingAddress = new HashMap<>();
		billingAddress.put("city", "Vancouver");
		billingAddress.put("country", "Canada");
		billingAddress.put("streetName", "123 Street name");
		billingAddress.put("postalCode", "ABC123");
		billingAddress.put("type", "billing");
		
		userAddresses.add(shippingAddress);
		userAddresses.add(billingAddress);
		
		Map<String, Object> userDetails = new HashMap<>();
		userDetails.put("firstName", "Manuel");
		userDetails.put("lastName", "Lorenzo");
		userDetails.put("email", USER_EMAIL);
		userDetails.put("password", USER_PASSWORD);
		userDetails.put("addresses", userAddresses);
		
		// Prepara la petición http
		Response response = given()
				.contentType(JSON_CONTENT_TYPE)
				.accept(JSON_ACCEPT)
				.body(userDetails)
				// Realiza la petición http
				.when()
				.post(CONTEXT_PATH + "/users")
				// Comprobaciones de la respuesta http
				.then()
				.statusCode(201)
				.contentType(JSON_CONTENT_TYPE)
				// Extrae la respuesta de la petición http
				.extract()
				.response();
		
		String userId = response.jsonPath().getString("userId");

		assertNotNull(userId);
		assertTrue(userId.length() == 30);
		
		JSONObject responseBodyJson;
		try {
			responseBodyJson = new JSONObject(response.body().asString());
			JSONArray addresses = responseBodyJson.getJSONArray("addresses");
			
			assertNotNull(addresses);
			assertTrue(addresses.length() == 2);
			
			String addressId = addresses.getJSONObject(0).getString("addressId");
			
			assertNotNull(addressId);
			assertTrue(addressId.length() == 30);
		}
		catch (JSONException e) {
			// Falla el test mostrando el mensaje de la excepción
			fail(e.getMessage());
		}

		MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
		
	    assertEquals(1, receivedMessages.length);

	    String bodyMessage= GreenMailUtil.getBody(receivedMessages[0]);
	    verifyEmailToken = bodyMessage.substring(bodyMessage.indexOf(VERIFY_EMAIL_TOKEN_PARAM) + VERIFY_EMAIL_TOKEN_PARAM.length(),
	    		bodyMessage.indexOf("'>"));
	}
	
	// Verify Email Test
	@Test
	void testB() {
		// Prepara la petición http
		Response response = given()
				.queryParam("token", verifyEmailToken)
				.accept(JSON_ACCEPT)
				// Realiza la petición http
				.when()
				.patch(CONTEXT_PATH + "/users/email-verification")
				// Comprobaciones de la respuesta http
				.then()
				.statusCode(200)
				.contentType(JSON_CONTENT_TYPE)
				// Extrae la respuesta de la petición http
				.extract()
				.response();
		
		String result = response.jsonPath().getString("operationResult");
		
		assertEquals("SUCCESS", result);
	}
	
	// User Login Test
	@Test
	void testC() {
		Map<String, Object> loginDetails = new HashMap<>();
		loginDetails.put("email", USER_EMAIL);
		loginDetails.put("password", USER_PASSWORD);
		
		// Prepara la petición http
		Response response =given()
				.contentType(JSON_CONTENT_TYPE)
				.body(loginDetails)
				// Realiza la petición http
				.when()
				.post(CONTEXT_PATH + "/users/login")
				// Comprobaciones de la respuesta http
				.then()
				.statusCode(200)
				// Extrae la respuesta de la petición http
				.extract()
				.response();
		
		authorizationHeader = response.header("Authorization");
		userId = response.header("UserId");
		
		assertNotNull(authorizationHeader);
		assertNotNull(userId);
	}
		
	// Get User Details Test
	@Test
	void testD() {
		// Prepara la petición http
		Response response = given()
				.pathParam("userId", userId)
				.header(AUTHORIZATION_HEADER_NAME, authorizationHeader)
				.accept(JSON_ACCEPT)
				// Realiza la petición http
				.when()
				.get(String.format("%s/users/{userId}", CONTEXT_PATH))
				// Comprobaciones de la respuesta http
				.then()
				.statusCode(200)
				.contentType(JSON_CONTENT_TYPE)
				// Extrae la respuesta de la petición http
				.extract()
				.response();
		
		String publicUserId = response.jsonPath().getString("userId");
		String userEmail = response.jsonPath().getString("email");
		String userFirstName = response.jsonPath().getString("firstName");
		String userLastName = response.jsonPath().getString("lastName");
		addresses = response.jsonPath().getList("addresses");
		String addressId = addresses.get(0).get("addressId");
		
		assertNotNull(publicUserId);
		assertNotNull(userEmail);
		assertNotNull(userFirstName);
		assertNotNull(userLastName);
		assertEquals(USER_EMAIL, userEmail);
		assertTrue(addresses.size() == 2);
		assertTrue(addressId.length() == 30);
	}
	
	// Update User Test
	@Test
	void testE() {
		final String newFirstName = "Manu";
		final String newLastName = "Loren";
		
		Map<String, Object> userDetails = new HashMap<>();
		userDetails.put("firstName", newFirstName);
		userDetails.put("lastName", newLastName);
		
		// Prepara la petición http
		Response response = given()
				.contentType(JSON_CONTENT_TYPE)
				.accept(JSON_ACCEPT)
				.header(AUTHORIZATION_HEADER_NAME, authorizationHeader)
				.pathParam("id", userId)
				.body(userDetails)
				// Realiza la petición http
				.when()
				.put(CONTEXT_PATH + "/users/{id}")
				// Comprobaciones de la respuesta http
				.then()
				.statusCode(200)
				.contentType(JSON_CONTENT_TYPE)
				// Extrae la respuesta de la petición http
				.extract()
				.response();
		
		String userFirstName = response.jsonPath().getString("firstName");
		String userLastName = response.jsonPath().getString("lastName");
		List<Map<String, String>> storedAddresses = response.jsonPath().getList("addresses");
		
		assertNotNull(userFirstName);
		assertNotNull(userLastName);
		assertTrue(userFirstName.equals(newFirstName));
		assertEquals(newLastName, userLastName);
		assertNotNull(storedAddresses);
		assertTrue(addresses.size() == storedAddresses.size());
		assertEquals(addresses.get(0).get("streetName"), storedAddresses.get(0).get("streetName"));	
	}
	
	// Delete User Test
	@Test
	void testF() {
		// Prepara la petición http
		given()
			.header(AUTHORIZATION_HEADER_NAME, authorizationHeader)
			.pathParam("id", userId)
			// Realiza la petición http
			.when()
			.delete(CONTEXT_PATH + "/users/{id}")
			// Comprobaciones de la respuesta http
			.then()
			.statusCode(204);
		
		// Prepara la petición http
		given()
			.pathParam("userId", userId)
			.header(AUTHORIZATION_HEADER_NAME, authorizationHeader)
			.accept(JSON_ACCEPT)
			// Realiza la petición http
			.when()
			.get(String.format("%s/users/{userId}", CONTEXT_PATH))
			// Comprobaciones de la respuesta http
			.then()
			// Se espera el código de estado 401 porque este endpoint requiere authorización y, como el usuario fue
			// eliminado del sistema previamente, la validación del token es inválida
			.statusCode(401);
	}
}
