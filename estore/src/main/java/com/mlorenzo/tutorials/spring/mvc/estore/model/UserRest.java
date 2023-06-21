package com.mlorenzo.tutorials.spring.mvc.estore.model;

public class UserRest {
	private final String id;
	private final String firstName;
	private final String lastName;
	private final String email;
	
	public UserRest(String id, String firstName, String lastName, String email) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}
}
