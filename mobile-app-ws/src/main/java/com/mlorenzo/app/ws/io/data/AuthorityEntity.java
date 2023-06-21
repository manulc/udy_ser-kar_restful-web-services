package com.mlorenzo.app.ws.io.data;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "authorities")
public class AuthorityEntity implements Serializable {

	private static final long serialVersionUID = -6622446003333759281L;
	
	@Id
	@GeneratedValue // Por defecto, la estrategia a usar es AUTO
	private long id;
	
	// Si no especificamos un tamaño, por defecto se utilizará el tamaño máximo de un VARCHAR(255 caracteres) para almacenar este campo en la tabla correspondiente de la base de datos
	// Para ser más eficientes, especificamos un tamaño de 20 caracteres
	@Column(length = 20, nullable = false)
	private String name;

	@ManyToMany(mappedBy = "authorities")
	private Collection<RoleEntity> roles;
	
	public AuthorityEntity( ) {
		
	}
	
	public AuthorityEntity(String name) {
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

	public Collection<RoleEntity> getRoles() {
		return roles;
	}

	public void setRoles(Collection<RoleEntity> roles) {
		this.roles = roles;
	}
}
