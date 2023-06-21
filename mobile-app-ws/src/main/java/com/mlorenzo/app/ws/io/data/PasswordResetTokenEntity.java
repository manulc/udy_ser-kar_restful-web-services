package com.mlorenzo.app.ws.io.data;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetTokenEntity implements Serializable {

	private static final long serialVersionUID = -1923425335307783443L;
	
	@Id
	@GeneratedValue // Por defecto, la estrategia a usar es AUTO
	private long id;
	
	private String token;
	
	@OneToOne
	// Por defecto, si no indicamos el nombre de la clave foránea, en este caso sería "user_details_id"
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private UserEntity userDetails;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public UserEntity getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(UserEntity userDetails) {
		this.userDetails = userDetails;
	}
	
}
