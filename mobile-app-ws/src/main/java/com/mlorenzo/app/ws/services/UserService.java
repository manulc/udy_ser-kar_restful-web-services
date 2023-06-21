package com.mlorenzo.app.ws.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.mlorenzo.app.ws.shared.dtos.UserDto;

public interface UserService extends UserDetailsService {
	UserDto createUser(UserDto user);
	List<UserDto> getUsers(int page, int limit);
	UserDto getUser(String email);
	UserDto getUserByUserId(String id);
	UserDto updateUser(UserDto user, String id);
	void deleteUser(String id);
	boolean verifyEmailToken(String token);
	boolean requestResetPassword(String email);
	boolean resetPassword(String token, String password);
}
