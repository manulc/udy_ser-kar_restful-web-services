package com.mlorenzo.app.ws;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.mlorenzo.app.ws.io.data.AuthorityEntity;
import com.mlorenzo.app.ws.io.data.RoleEntity;
import com.mlorenzo.app.ws.io.data.UserEntity;
import com.mlorenzo.app.ws.io.repositories.AuthorityRepository;
import com.mlorenzo.app.ws.io.repositories.RoleRepository;
import com.mlorenzo.app.ws.io.repositories.UserRepository;
import com.mlorenzo.app.ws.shared.Roles;
import com.mlorenzo.app.ws.shared.Utils;

@Component
public class InitialUserSetup {

	@Autowired
	private AuthorityRepository authorityRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private Utils utils;
	
	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;
	
	@Transactional
	@EventListener
	public void onApplicationEvent(ApplicationReadyEvent event) {
		System.out.println("From Application ready event...");
		
		if(userRepository.count() == 0) {
			AuthorityEntity readAuthority = createAuthority("READ_AUTHORITY");
			AuthorityEntity writeAuthority = createAuthority("WRITE_AUTHORITY");
			AuthorityEntity deleteAuthority = createAuthority("DELETE_AUTHORITY");
			
			createRole(Roles.ROLE_USER.name(), List.of(readAuthority, writeAuthority));
			RoleEntity roleAdmin = createRole(Roles.ROLE_ADMIN.name(), List.of(readAuthority, writeAuthority, deleteAuthority));
		
			UserEntity adminUser = new UserEntity();
			adminUser.setFirstName("Manuel");
			adminUser.setLastName("Lorenzo");
			adminUser.setEmail("admin@test.com");
			adminUser.setEmailVerificationStatus(Boolean.TRUE);
			adminUser.setUserId(utils.generateId(30));
			adminUser.setEncryptedPassword(bCryptPasswordEncoder.encode("12345678"));
			adminUser.setRoles(Arrays.asList(roleAdmin));
			
			userRepository.save(adminUser);
		}
	}
	
	private AuthorityEntity createAuthority(String name) {
		AuthorityEntity authorityEntity = authorityRepository.findByName(name);
		
		if(authorityEntity == null) {
			authorityEntity = new AuthorityEntity(name);
			return authorityRepository.save(authorityEntity);
		}
		
		return authorityEntity;
	}
	
	private RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
		RoleEntity roleEntity = roleRepository.findByName(name);
		
		if(roleEntity == null) {
			roleEntity = new RoleEntity(name);
			roleEntity.setAuthorities(authorities);
			return roleRepository.save(roleEntity);
		}
		
		return roleEntity;
	}
}
