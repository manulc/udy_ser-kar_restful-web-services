package com.mlorenzo.app.ws.io.repositories;

import org.springframework.data.repository.CrudRepository;

import com.mlorenzo.app.ws.io.data.AuthorityEntity;

public interface AuthorityRepository extends CrudRepository<AuthorityEntity, Long> {
	AuthorityEntity findByName(String name);
}
