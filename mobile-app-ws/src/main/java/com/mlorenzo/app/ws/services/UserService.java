package com.mlorenzo.app.ws.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.mlorenzo.app.ws.ui.models.requests.UserDetailsRequestModel;
import com.mlorenzo.app.ws.ui.models.responses.UserRest;

public interface UserService extends UserDetailsService {
	UserRest createUser(UserDetailsRequestModel userDetails);
	List<UserRest> getUsers(int page, int limit);
	UserRest getUser(String email);
	UserRest getUserByUserId(String id);
	UserRest updateUser(UserDetailsRequestModel userDetails, String id);
	void deleteUser(String id);
	boolean verifyEmailToken(String token);
	boolean requestResetPassword(String email);
	boolean resetPassword(String token, String password);
}
