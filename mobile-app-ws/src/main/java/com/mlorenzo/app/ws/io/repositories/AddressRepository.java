package com.mlorenzo.app.ws.io.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.mlorenzo.app.ws.io.data.AddressEntity;
import com.mlorenzo.app.ws.io.data.UserEntity;

public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
	// Nota: En este caso, también valdría llamar a este método "findByUserDetails"
	// Es decir, tanto si se llama "findAllByUserDetails" como "findByUserDetails", se devuelve todas las direcciones del usuario
	List<AddressEntity> findAllByUserDetails(UserEntity userDetails);
	
	AddressEntity findByAddressId(String addressId);
}
