package com.mlorenzo.app.ws.services;

import java.util.List;

import com.mlorenzo.app.ws.ui.models.responses.AddressRest;

public interface AddressService {
	List<AddressRest> getAddresses(String userId);
	AddressRest getAddress(String userId, String addressId);
}
