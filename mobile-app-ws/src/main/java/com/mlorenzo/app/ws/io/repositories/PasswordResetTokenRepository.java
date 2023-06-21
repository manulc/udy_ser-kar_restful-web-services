package com.mlorenzo.app.ws.io.repositories;

import org.springframework.data.repository.CrudRepository;

import com.mlorenzo.app.ws.io.data.PasswordResetTokenEntity;
import com.mlorenzo.app.ws.io.data.UserEntity;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long> {
	PasswordResetTokenEntity findByToken(String token);
	boolean existsByUserDetails(UserEntity user);
}
