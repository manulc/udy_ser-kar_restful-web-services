package com.mlorenzo.app.ws.io.repositories;

import org.springframework.data.repository.CrudRepository;

import com.mlorenzo.app.ws.io.data.RoleEntity;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
	RoleEntity findByName(String name);
}
