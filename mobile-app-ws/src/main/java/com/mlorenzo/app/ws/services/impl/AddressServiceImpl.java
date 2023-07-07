package com.mlorenzo.app.ws.services.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mlorenzo.app.ws.exceptions.UserServiceException;
import com.mlorenzo.app.ws.io.data.AddressEntity;
import com.mlorenzo.app.ws.io.data.UserEntity;
import com.mlorenzo.app.ws.io.repositories.AddressRepository;
import com.mlorenzo.app.ws.io.repositories.UserRepository;
import com.mlorenzo.app.ws.services.AddressService;
import com.mlorenzo.app.ws.ui.models.responses.AddressRest;
import com.mlorenzo.app.ws.ui.models.responses.ErrorMessages;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public List<AddressRest> getAddresses(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId);
		if(userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND);
		Type listType = new TypeToken<List<AddressRest>>() {}.getType();
		return modelMapper.map(addressRepository.findAllByUserDetails(userEntity), listType);
	}

	@Override
	public AddressRest getAddress(String userId, String addressId) {
		if(userRepository.findByUserId(userId) == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND);
		AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
		if(addressEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage(), HttpStatus.NOT_FOUND);
		return modelMapper.map(addressEntity, AddressRest.class);
	}
}
