package com.mlorenzo.tutorials.spring.mvc.estore.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.mlorenzo.tutorials.spring.mvc.estore.model.User;
import com.mlorenzo.tutorials.spring.mvc.estore.model.UserRest;

@Controller
public class UserController {

	@GetMapping("/users/{id}/albums/{albumId}")
	public ModelAndView getAlbum(@PathVariable(value = "id") String userId, @PathVariable String albumId) {
		ModelAndView modelAndView = new ModelAndView("album");
		modelAndView.addObject("userId", userId);
		modelAndView.addObject("albumId", albumId);
		return modelAndView;
	}
	
	@GetMapping("/users")
	public ModelAndView getUsers(@RequestParam(defaultValue = "30") int limit) {
		ModelAndView modelAndView = new ModelAndView("users");
		modelAndView.addObject("limit", limit);
		return modelAndView;
	}
	
	@GetMapping("/signup")
	public String signupForm() {
		return "signup";
	}
	
	// La anotación @ModelAttribute nos permite leer datos desde un formulario en una vista para mapearlos a un objeto
	// y, automáticamente, ese objeto se convierte en un atributo de la vista que devuelve el método
	@PostMapping("/signup")
	public String signupFormSubmit(@ModelAttribute User user) {
		// Debido a la anotación @ModelAttribute, automáticamente el objeto "user" pasa a ser un atributo de esta vista "signup-result"
		return "signup-result";
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	@PostMapping("/users")
	public UserRest createUser(@RequestBody User user) {
		return new UserRest(UUID.randomUUID().toString(), user.getFirstName(), user.getLastName(), user.getEmail());
	}
	
	@PostMapping("/users/response-entity")
	public ResponseEntity<UserRest> createUserResponseEntity(@RequestBody User user) {
		UserRest userRest = new UserRest(UUID.randomUUID().toString(), user.getFirstName(), user.getLastName(), user.getEmail());
		return new ResponseEntity<>(userRest, HttpStatus.CREATED);
	}
}
