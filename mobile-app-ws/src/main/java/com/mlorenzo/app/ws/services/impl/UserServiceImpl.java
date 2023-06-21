package com.mlorenzo.app.ws.services.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mlorenzo.app.ws.exceptions.UserServiceException;
import com.mlorenzo.app.ws.io.data.PasswordResetTokenEntity;
import com.mlorenzo.app.ws.io.data.RoleEntity;
import com.mlorenzo.app.ws.io.data.UserEntity;
import com.mlorenzo.app.ws.io.repositories.PasswordResetTokenRepository;
import com.mlorenzo.app.ws.io.repositories.RoleRepository;
import com.mlorenzo.app.ws.io.repositories.UserRepository;
import com.mlorenzo.app.ws.security.UserPrincipal;
import com.mlorenzo.app.ws.services.UserService;
import com.mlorenzo.app.ws.shared.EmailSender;
import com.mlorenzo.app.ws.shared.Roles;
import com.mlorenzo.app.ws.shared.Utils;
import com.mlorenzo.app.ws.shared.dtos.UserDto;
import com.mlorenzo.app.ws.ui.models.responses.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private Utils utils;
	
	@Autowired
	private EmailSender emailSender;
	
	@Autowired
	private PasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private ModelMapper modelMapper;

	@Override
	public UserDto createUser(UserDto user) {
		// No puede haber usuarios en la base de datos con el mismo email
		if(userRepository.findByEmail(user.getEmail()) != null)
			throw new UserServiceException(ErrorMessages.RECORD_ALREADY_EXISTS.getErrorMessage(), HttpStatus.BAD_REQUEST);
	
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);
		
		String publicUserId = utils.generateId(30);
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setEmailVerificationStatus(Boolean.FALSE);
		userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
		userEntity.getAddresses().forEach(aEntity -> {
			aEntity.setAddressId(utils.generateId(30));
			// Establece la relación bidireccional
			aEntity.setUserDetails(userEntity);
		});
		
		RoleEntity roleEntity = roleRepository.findByName(Roles.ROLE_USER.name());
		
		if(roleEntity != null)
			userEntity.setRoles(new HashSet<>(Arrays.asList(roleEntity)));
		
		UserEntity storedUser = userRepository.save(userEntity);
		
		UserDto userDto = modelMapper.map(storedUser, UserDto.class);
		
		emailSender.sendVerifyEmail(userDto);
		
		return userDto;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(userEntity == null)
			throw new UsernameNotFoundException(email);
		
		// Se comenta porque ahora tenemos que crear una colección de tipo "GrantedAuthority" de Spring Security que contenga los roles y los authorities o permisos del usuario
		// Entonces, para no poner aquí toda la lógica para crear dicha colección, ahora usamos nuestra clase "UserPrincipal" que implementa la interfaz "UserDetails" de Spring Security
		//return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	
		return new UserPrincipal(userEntity);
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(userEntity == null)
			throw new UsernameNotFoundException(email);
		
		// Nota: Aquí no usamos ModelMapper porque no necesitamos obtener los datos de las direcciones del usuario,
		// ya que este método únicamente se utiliza en nuestro filtro de seguridad "AuthenticationFilter" para saber si el usuario existe o no en la base de datos a través del email proporcionado
		//UserDto userDto = modelMapper.map(userEntity, UserDto.class);
		
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userEntity, userDto);
		
		return userDto;
	}
	
	@Override
	public List<UserDto> getUsers(int page, int limit) {
		Pageable pageableRequest = PageRequest.of(page, limit);
		
		Page<UserDto> usersDtoPage = userRepository.findAll(pageableRequest)
				.map(userEntity -> modelMapper.map(userEntity, UserDto.class));
		
		return usersDtoPage.getContent();
	}

	@Override
	public UserDto getUserByUserId(String id) {
		UserEntity userEntity = userRepository.findByUserId(id);
		
		if(userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND);
		
		return modelMapper.map(userEntity, UserDto.class);
	}

	@Override
	public UserDto updateUser(UserDto user, String id) {
		UserEntity userEntity = userRepository.findByUserId(id);
		
		if(userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND);
		
		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());
		
		UserEntity updatedUserDetails = userRepository.save(userEntity);
		
		return modelMapper.map(updatedUserDetails, UserDto.class);
	}

	@Override
	public void deleteUser(String id) {
		UserEntity userEntity = userRepository.findByUserId(id);
		
		if(userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND);
		
		userRepository.delete(userEntity);
	}

	@Override
	public boolean verifyEmailToken(String token) {
		UserEntity userEntity = userRepository.findByEmailVerificationToken(token);
		if(userEntity != null && !utils.hasTokenExpired(token)) {
			userEntity.setEmailVerificationToken(null);
			userEntity.setEmailVerificationStatus(Boolean.TRUE);
			userRepository.save(userEntity);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean requestResetPassword(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		if(userEntity == null)
			return false; 
		if(passwordResetTokenRepository.existsByUserDetails(userEntity))
			return false;
		PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
		String token = utils.generatePasswordResetToken(userEntity.getUserId());
		passwordResetTokenEntity.setToken(token);
		passwordResetTokenEntity.setUserDetails(userEntity);
		passwordResetTokenRepository.save(passwordResetTokenEntity);
		emailSender.sendPasswordResetRequestEmail(userEntity.getFirstName(), userEntity.getEmail(), token);
		return true;
	}

	@Override
	public boolean resetPassword(String token, String password) {
		PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);
		if(passwordResetTokenEntity == null)
			return false;
		if(utils.hasTokenExpired(token)) {
			passwordResetTokenRepository.delete(passwordResetTokenEntity);
			return false;
		}
		UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(password));
		userRepository.save(userEntity);
		passwordResetTokenRepository.delete(passwordResetTokenEntity);
		return true;
	}

}
