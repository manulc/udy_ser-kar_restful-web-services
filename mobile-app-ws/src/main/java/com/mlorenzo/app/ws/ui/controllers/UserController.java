package com.mlorenzo.app.ws.ui.controllers;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mlorenzo.app.ws.services.AddressService;
import com.mlorenzo.app.ws.services.UserService;
import com.mlorenzo.app.ws.shared.RequestOperationName;
import com.mlorenzo.app.ws.shared.RequestOperationStatus;
import com.mlorenzo.app.ws.shared.dtos.UserDto;
import com.mlorenzo.app.ws.ui.models.requests.PasswordResetModel;
import com.mlorenzo.app.ws.ui.models.requests.PasswordResetRequestModel;
import com.mlorenzo.app.ws.ui.models.requests.UserDetailsRequestModel;
import com.mlorenzo.app.ws.ui.models.responses.AddressRest;
import com.mlorenzo.app.ws.ui.models.responses.OperationStatusModel;
import com.mlorenzo.app.ws.ui.models.responses.UserRest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

// Nota: En el archivo de propiedades se ha cambiado el "context path" a "/mobile-app-ws". Así que, ahora, la ruta base es "http://localhost:8080/mobile-app-ws/users"
// Nota sobre CORS: 2 o más orígenes se consideran iguales si tienen el mismo protocolo o esquema(http o https), el mismo host y el mismo puerto

// Ejemplo para habilitar CORS en todos los métodos handler(ya que la anotación está a nivel de clase) de un determinado controlador REST
// Se comenta porque ahora usamos una configuración global de CORS que afecto a todos los métodos handler de todos los controladores REST
//@CrossOrigin({"http://localhost:8083", "http://localhost:8084"}) // En este caso, se permite realizar peticiones http a este método únicamente a los orígenes "http://localhost:8083" y "http://localhost:8084"
@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	// Nota: Si no se indica el atributo "produces" de las anotaciones @GetMapping, @PostMapping, @PutMapping, etc.., y no existe la cabecera "Accept" en la petición http, por defecto se devuelven los datos en formato Json
	// Ahora, como si existe el atributo "produces" en los métodos correspondientes y el MediaType XML es el que aparece en primer lugar en la lista, si no se existe la cabecera "Accept" en la petición http, por defecto los datos se devuelven en formato XML
	
	
	// Como esta ruta o endpoint requiere autenticación mediante un token JWT en la cabecera de cada petición http que se haga, usamos esta anotación de Swagger para que añada en su interfaz web un parámetro implícito(un campo de formulario) que nos permita insertar dicho token para poder realizar peticiones http desde su interfaz web
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
	})
	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public List<UserRest> getUsers(@RequestParam(defaultValue = "0" ) int page,
			@RequestParam(defaultValue = "25" ) int limit) {
		Type listType = new TypeToken<List<UserRest>>() {}.getType();
		
		return modelMapper.map(userService.getUsers(page, limit), listType);
		
		// Otra alternativa equivalente para realizar el mapeo
		/* return userService.getUsers(page, limit).stream()
				.map(userDto -> modelMapper.map(userDto, UserRest.class))
				.collect(Collectors.toList()); */
	}

	// Como esta ruta o endpoint requiere autenticación mediante un token JWT en la cabecera de cada petición http que se haga, usamos esta anotación de Swagger para que añada en su interfaz web un parámetro implícito(un campo de formulario) que nos permita insertar dicho token para poder realizar peticiones http desde su interfaz web
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
	})
	@ApiOperation(
			value = "${userController.GetUser.ApiOperation.Value}",
			notes = "${userController.GetUser.ApiOperation.Notes}")
	//@PostAuthorize("hasRole('ADMIN')") // También valdría @PostAuthorize("hasAuthority('ROLE_ADMIN')")
	@PostAuthorize("hasRole('ADMIN') or returnObject.userId == principal.userEntity.userId") // En este caso, "returnObject" es un objeto de tipo "UserRest" y "principal" es un objeto de tipo "UserPrincipal"
	@GetMapping(path = "/{userId}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest getUser(@PathVariable(value = "userId") String id) {
		UserDto userDto = userService.getUserByUserId(id);
		
		return modelMapper.map(userDto, UserRest.class);
	}
	
	// Como esta ruta o endpoint requiere autenticación mediante un token JWT en la cabecera de cada petición http que se haga, usamos esta anotación de Swagger para que añada en su interfaz web un parámetro implícito(un campo de formulario) que nos permita insertar dicho token para poder realizar peticiones http desde su interfaz web
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
	})
	@GetMapping(path = "/{userId}/addresses", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public Resources<AddressRest> getUserAddresses(@PathVariable String userId) {
		List<AddressRest> addresses = addressService.getAddresses(userId).stream()
				.map(addressDto -> {
					AddressRest addressRest = modelMapper.map(addressDto, AddressRest.class);
					
					// Enlace de Hateoas al propio recurso(withSelfRel)
					Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressRest.getAddressId())).withSelfRel();
					addressRest.add(addressLink);
					
					return addressRest;
				})
				.collect(Collectors.toList());
		
		// Enlace de Hateoas al propio recurso(withSelfRel)
		Link addressLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withSelfRel();
		
		// Enlace de Hateoas al recurso relacionado con el usuario(withRel)
		Link userLink = linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");
		
		Resources<AddressRest> resources = new Resources<>(addresses);
		resources.add(userLink);
		resources.add(addressLink);
		
		return resources;
	}
	
	// Ejemplo para habilitar CORS en un método handler específico de un determinado controlador REST
	// Se comenta porque ahora usamos esta anotación a nivel de clase afectando a todos los métodos handler de este controlador REST
	//@CrossOrigin(origins = "http://localhost:8084") // En este caso, se permite realizar peticiones http a este método únicamente al origen "http://localhost:8084"
	//@CrossOrigin(origins = {"http://localhost:8083", "http://localhost:8084"}) // En este caso, se permite realizar peticiones http a este método únicamente a los orígenes "http://localhost:8083" y "http://localhost:8084"
	//@CrossOrigin(origins = "*") // En este caso, se permite realizar peticiones http a este método a cualquier origen
	@PatchMapping(path = "/email-verification", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel verifyEmailAddress(@RequestParam("token") String token) {
		OperationStatusModel operationStatusModel = new OperationStatusModel();
		operationStatusModel.setOperationName(RequestOperationName.VERIFY_EMAIL);
		if(userService.verifyEmailToken(token))
			operationStatusModel.setOperationResult(RequestOperationStatus.SUCCESS);
		else
			operationStatusModel.setOperationResult(RequestOperationStatus.ERROR);
		return operationStatusModel;
	}
	
	@PostMapping(path = "/password-reset-request",
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
			consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel requestResetPassword(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
		OperationStatusModel operationStatusModel = new OperationStatusModel();
		operationStatusModel.setOperationName(RequestOperationName.REQUEST_RESET_PASSWORD);
		if(userService.requestResetPassword(passwordResetRequestModel.getEmail()))
			operationStatusModel.setOperationResult(RequestOperationStatus.SUCCESS);
		else
			operationStatusModel.setOperationResult(RequestOperationStatus.ERROR);
		return operationStatusModel;
	}
	
	@PatchMapping(path = "/password-reset",
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
			consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
		OperationStatusModel operationStatusModel = new OperationStatusModel();
		operationStatusModel.setOperationName(RequestOperationName.RESET_PASSWORD);
		if(userService.resetPassword(passwordResetModel.getToken(), passwordResetModel.getPassword()))
			operationStatusModel.setOperationResult(RequestOperationStatus.SUCCESS);
		else
			operationStatusModel.setOperationResult(RequestOperationStatus.ERROR);
		return operationStatusModel;
	}
	
	// Como esta ruta o endpoint requiere autenticación mediante un token JWT en la cabecera de cada petición http que se haga, usamos esta anotación de Swagger para que añada en su interfaz web un parámetro implícito(un campo de formulario) que nos permita insertar dicho token para poder realizar peticiones http desde su interfaz web
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
	})
	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public Resource<AddressRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
		// Enlace de Hateoas al propio recurso(withSelfRel)
		//Link addressLink = linkTo(UserController.class).slash(userId).slash("addresses").slash(addressId).withSelfRel();
		
		// Enlace de Hateoas al propio recurso(withSelfRel)
		// Para eviar tener que hardcodear valores(En este caso el texto "addresses") en los enlaces, podemos usar el método estático "methodOn" de la siguiente manera
		Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
		
		// Enlace de Hateoas al recurso relacionado con el usuario(withRel)
		Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
		
		// Enlace de Hateoas al recurso relacionado con el usuario(withRel)
		//Link addressesLink = linkTo(UserController.class).slash(userId).slash("addresses").withRel("addresses");
		
		// Enlace de Hateoas al recurso relacionado con el usuario(withRel)
		// Para eviar tener que hardcodear valores(En este caso el texto "addresses") en los enlaces, podemos usar el método estático "methodOn" de la siguiente manera
		Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
		
		AddressRest addressRest = modelMapper.map(addressService.getAddress(userId, addressId), AddressRest.class);
		// Añadimos los enlaces de Hateoas
		addressRest.add(addressLink);
		addressRest.add(userLink);
		addressRest.add(addressesLink);
		
		return new Resource<>(addressRest);
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(
			consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
	)
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		
		UserDto createdUser = userService.createUser(userDto);
		
		UserRest userRest = modelMapper.map(createdUser, UserRest.class);
		
		return userRest;
	}
	
	// Como esta ruta o endpoint requiere autenticación mediante un token JWT en la cabecera de cada petición http que se haga, usamos esta anotación de Swagger para que añada en su interfaz web un parámetro implícito(un campo de formulario) que nos permita insertar dicho token para poder realizar peticiones http desde su interfaz web
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
	})
	@PutMapping(
			path = "/{userId}",
			consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE },
			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
	)
	public UserRest updateUser(@RequestBody UserDetailsRequestModel userDetails, @PathVariable String userId) {
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		
		UserDto updatedUser = userService.updateUser(userDto, userId);
		
		UserRest userRest = modelMapper.map(updatedUser, UserRest.class);
		
		return userRest;
	}
	
	// Como esta ruta o endpoint requiere autenticación mediante un token JWT en la cabecera de cada petición http que se haga, usamos esta anotación de Swagger para que añada en su interfaz web un parámetro implícito(un campo de formulario) que nos permita insertar dicho token para poder realizar peticiones http desde su interfaz web
	@ApiImplicitParams({
		@ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
	})
	//@Secured("ROLE_ADMIN")
	//@PreAuthorize("hasRole('ADMIN')") // También valdría @PreAuthorize("hasAuthority('ROLE_ADMIN')")
	//@PreAuthorize("hasAuthority('DELETE_AUTHORITY')")
	@PreAuthorize("hasRole('ADMIN') or #userId == principal.userEntity.userId") // "#userId" hace referencia al nombre del argumento de entrada de este método y "principal" es, en nuestro caso, un objeto de tipo "UserPrincipal"
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{userId}")
	public void deleteUser(@PathVariable String userId) {
		userService.deleteUser(userId);
	}
}
