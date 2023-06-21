package com.mlorenzo.app.ws.io.data;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
public class RoleEntity implements Serializable {

	private static final long serialVersionUID = 2026872559792123322L;

	@Id
	@GeneratedValue // Por defecto, la estrategia a usar es AUTO
	private long id;
	
	// Si no especificamos un tamaño, por defecto se utilizará el tamaño máximo de un VARCHAR(255 caracteres) para almacenar este campo en la tabla correspondiente de la base de datos
	// Para ser más eficientes, especificamos un tamaño de 20 caracteres
	@Column(length = 20, nullable = false)
	private String name;
	
	@ManyToMany(mappedBy = "roles")
	private Collection<UserEntity> users;
	
	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	// Si queremos personalizar la tabla intermedia que se crea para establecer las relaciones entre roles y authorities o permisos, usamos la anotación @JoinTable de esta manera
	// Se comenta porque, en este caso, los valores de esta anotación son los valores por defecto
	/*@JoinTable(
			name = "roles_authorities",
			joinColumns = @JoinColumn(name = "roles_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "authorities_id", referencedColumnName = "id")) */
	private Collection<AuthorityEntity> authorities;
	
	public RoleEntity() {
		
	}
	
	public RoleEntity(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<UserEntity> getUsers() {
		return users;
	}

	public void setUsers(Collection<UserEntity> users) {
		this.users = users;
	}

	public Collection<AuthorityEntity> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<AuthorityEntity> authorities) {
		this.authorities = authorities;
	}
}
