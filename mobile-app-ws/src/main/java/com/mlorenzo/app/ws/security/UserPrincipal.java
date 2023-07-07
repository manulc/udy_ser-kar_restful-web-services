package com.mlorenzo.app.ws.security;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mlorenzo.app.ws.io.data.AuthorityEntity;
import com.mlorenzo.app.ws.io.data.UserEntity;

public class UserPrincipal implements UserDetails {
	private static final long serialVersionUID = -1335045060468400369L;
	
	private UserEntity userEntity;

	public UserPrincipal(UserEntity userEntity) {
		this.userEntity = userEntity;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Usamos "HashSet" para asegurar que los roles y authorities, o permisos, son únicos en la colección final de tipo "GrantedAuthority"
		Collection<GrantedAuthority> grantedAuthorities = new HashSet<>();
		Collection<AuthorityEntity> authorityEntities = new HashSet<>();
		userEntity.getRoles().forEach(roleEntity -> {
			grantedAuthorities.add(new SimpleGrantedAuthority(roleEntity.getName()));
			authorityEntities.addAll(roleEntity.getAuthorities());
		});
		authorityEntities.forEach(authorityEntity -> grantedAuthorities.add(new SimpleGrantedAuthority(authorityEntity.getName())));
		return grantedAuthorities;
	}

	@Override
	public String getPassword() {
		return userEntity.getEncryptedPassword();
	}

	@Override
	public String getUsername() {
		return userEntity.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return userEntity.getEmailVerificationStatus();
	}

	// Para poder usar este objeto en las expresiones de las anotaciones de seguridad a nivel de métodos @PreAuthorize y @PostAuthorize
	public UserEntity getUserEntity() {
		return userEntity;
	}
}
