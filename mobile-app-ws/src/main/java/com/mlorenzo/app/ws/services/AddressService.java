package com.mlorenzo.app.ws.services;

import java.util.List;

import com.mlorenzo.app.ws.shared.dtos.AddressDto;

public interface AddressService {
	List<AddressDto> getAddresses(String userId);
	AddressDto getAddress(String userId, String addressId);
}
