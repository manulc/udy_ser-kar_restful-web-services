package com.mlorenzo.app.ws.io.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity implements Serializable {

	private static final long serialVersionUID = 4604394119633450618L;
	
	@Id
	@GeneratedValue // Por defecto, la estrategia a usar es AUTO
	private long id;
	
	// Se comenta porque ya no hace falta la restricción "unique = true" debido a que ahora se genere un id único programáticamente para cada usuario que se registre en la base de datos
	//@Column(nullable = false, unique = true)
	@Column(nullable = false)
	private String userId;
	
	// Si no especificamos un tamaño, por defecto se utilizará el tamaño máximo de un VARCHAR(255 caracteres) para almacenar este campo en la tabla correspondiente de la base de datos
	// Para ser más eficientes, especificamos un tamaño de 50 caracteres
	@Column(nullable = false, length = 50)
	private String firstName;
	
	@Column(nullable = false, length = 50)
	private String lastName;
	
	// Se comenta porque ya no hace falta la restricción "unique = true" debido a ahora se comprueba programáticamente, antes de crear un nuevo usuario en la base de datos, si existe previamente otro usuario con el mismo email
	//@Column(nullable = false, length = 120, unique = true)
	@Column(nullable = false, length = 120)
	private String email;
	
	@Column(nullable = false)
	private String encryptedPassword;
	
	private String emailVerificationToken;
	
	@Column(nullable = false)
	private Boolean emailVerificationStatus;
	
	@OneToMany(mappedBy = "userDetails", cascade = CascadeType.ALL)
	private List<AddressEntity> addresses;
	
	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	// Si queremos personalizar la tabla intermedia que se crea para establecer las relaciones entre usuarios y roles, usamos la anotación @JoinTable de esta manera
	// Se comenta porque, en este caso, los valores de esta anotación son los valores por defecto
	/*@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(name = "users_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "roles_id", referencedColumnName = "id")) */
	private Collection<RoleEntity> roles;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public String getEmailVerificationToken() {
		return emailVerificationToken;
	}

	public void setEmailVerificationToken(String emailVerificationToken) {
		this.emailVerificationToken = emailVerificationToken;
	}

	public Boolean getEmailVerificationStatus() {
		return emailVerificationStatus;
	}

	public void setEmailVerificationStatus(Boolean emailVerificationStatus) {
		this.emailVerificationStatus = emailVerificationStatus;
	}

	public List<AddressEntity> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<AddressEntity> addresses) {
		this.addresses = addresses;
	}

	public Collection<RoleEntity> getRoles() {
		return roles;
	}

	public void setRoles(Collection<RoleEntity> roles) {
		this.roles = roles;
	}
}
