package com.mlorenzo.app.ws.io.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.mlorenzo.app.ws.io.data.UserEntity;

// Nota: Si es necesario escribir consultas manualmente, es recomendable usar JPQL sobre consultas nativas SQL porque JPQL es independiente a los tipos de bases de datos y las consultas JPQL siempre funcionarán.
// Sin embargo, las consultas nativas SQL pueden dejar de funcionar bien si en un futuro se cambia el tipo de base de datos ya que la sintaxis de esas consultas puede cambiar

public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {
	UserEntity findByEmail(String email);
	UserEntity findByUserId(String id);
	UserEntity findByEmailVerificationToken(String token);
	
	// En este caso, no estamos siguiendo la nomenclatura propia de Spring Data JPA y, por esta razón, escribimos la consulta manualmente
	// Para ello, hemos decidido usar el lenguaje SQL nativo.
	// Por otra parte, como estamos escribiendo la consulta manualmente y como estamos devolviendo datos con paginación, es recomendable(pero opcional) indicar también
	// la consulta SQL que obtiene el número total de registros que cumplen la condición para que Spring Data JPA pueda calcular la paginación de forma más optimizada 
	@Query(value = "select * from Users u where u.email_verification_status = 'true'",
			countQuery = "select count(*) from Users u where u.email_verification_status = 'true'",
			nativeQuery = true)
	Page<UserEntity> findAllUsersWithConfirmedEmailAddress(Pageable pageableRequest);
	
	// En este caso, tampoco estamos siguiendo la nomenclatura propia de Spring Data JPA y, por esta razón, escribimos la consulta manualmente
	// Para ello, hemos decidido usar el lenguaje SQL nativo.
	// '?1' hace referencia al valor del primer argumento de entrada(y único en este caso) del método
	@Query(value = "select * from Users u where u.first_name = ?1", nativeQuery = true)
	List<UserEntity> findUsersByFirstName(String firstName);
	
	// En este caso, tampoco estamos siguiendo la nomenclatura propia de Spring Data JPA y, por esta razón, escribimos la consulta manualmente
	// Para ello, hemos decidido usar el lenguaje SQL nativo.
	// ':lastName' hace referencia al valor del argumento de entrada del método que coincide con ese nombre. Si no coincide el nombre, debe usarse la anotación @Param para asociar el argumento de entrada con el parámetro de la consulta SQL
	// En este caso, indicar la anotación @Param es opcional porque tanto el nombre del parámetro de la consulta como el nombre del argumento de entrada que queremos asociar son iguales
	@Query(value = "select * from Users u where u.last_name = :lastName", nativeQuery = true)
	List<UserEntity> findUsersByLastName(@Param(value = "lastName") String lastName);

	// En este caso, tampoco estamos siguiendo la nomenclatura propia de Spring Data JPA y, por esta razón, escribimos la consulta manualmente
	// Para ello, hemos decidido usar el lenguaje SQL nativo.
	// ':keyboard' hace referencia al valor del argumento de entrada del método asociado mediante la anotación @Param
	// En este caso, indicar la anotación @Param es obligatorio porque el nombre del parámetro de la consulta es distinto al nombre del argumento de entrada que queremos asociar
	@Query(value = "select * from Users u where u.first_name like %:keyboard% or u.last_name like %:keyboard%", nativeQuery = true)
	List<UserEntity> findUsersByKeyboard(@Param(value = "keyboard") String term);
	
	// En este caso, tampoco estamos siguiendo la nomenclatura propia de Spring Data JPA y, por esta razón, escribimos la consulta manualmente
	// Para ello, hemos decidido usar el lenguaje SQL nativo.
	// ':keyboard' hace referencia al valor del argumento de entrada del método asociado mediante la anotación @Param
	// En este caso, indicar la anotación @Param es obligatorio porque el nombre del parámetro de la consulta es distinto al nombre del argumento de entrada que queremos asociar
	@Query(value = "select u.first_name,u.last_name from Users u where u.first_name like %:keyboard% or u.last_name like %:keyboard%", nativeQuery = true)
	List<Object[]> findUsersFirstNameAndLastNameByKeyboard(@Param(value = "keyboard") String term);

	// En este caso, tampoco estamos siguiendo la nomenclatura propia de Spring Data JPA y, por esta razón, escribimos la consulta manualmente
	// Para ello, hemos decidido usar el lenguaje SQL nativo.
	// ':emailVerificationStatus' y ':userId' hacen referencia a los valores de los argumentos de entrada del método "emailVerificationStatus" y "userId" respectivamente
	// En este caso, indicar la anotación @Param es opcional porque los nombres de ámbos parámetros de la sentencia SQL coinciden con los nombres de ámbos argumentos de entrada del método
	@Transactional
	@Modifying // Como esta consulta SQL modifica un registro en la tabla correspondiente de la base de datos y no es una consulta, tenemos que indicar esta anotación 
	@Query(value = "update users u set u.email_verification_status=:emailVerificationStatus where u.user_id=:userId", nativeQuery = true)
	void updateUserEmailVerificationStatus(boolean emailVerificationStatus, String userId);
	
	// En este caso, tampoco estamos siguiendo la nomenclatura propia de Spring Data JPA y, por esta razón, escribimos la consulta manualmente
	// Para ello, hemos decidido usar el lenguaje JPQL(Java Persistence Query Languague).
	// '?1' hace referencia al valor del primer argumento de entrada(y único en este caso) del método
	@Query("select u from UserEntity u where u.userId=?1")
	UserEntity findUserEntityByUserId(String userId);
	
	// En este caso, tampoco estamos siguiendo la nomenclatura propia de Spring Data JPA y, por esta razón, escribimos la consulta manualmente
	// Para ello, hemos decidido usar el lenguaje JPQL(Java Persistence Query Languague).
	// ':userId' hace referencia al valor del argumento de entrada del método que coincide con ese nombre. Si no coincide el nombre, debe usarse la anotación @Param para asociar el argumento de entrada con el parámetro de la consulta JPQL
	// En este caso, indicar la anotación @Param es opcional porque tanto el nombre del parámetro de la consulta como el nombre del argumento de entrada que queremos asociar son iguales
	@Query("select user.firstName, user.lastName from UserEntity user where user.userId=:userId")
	List<Object[]> getUserFullNameByUserId(@Param(value = "userId") String userId);
	
	// En este caso, tampoco estamos siguiendo la nomenclatura propia de Spring Data JPA y, por esta razón, escribimos la consulta manualmente
	// Para ello, hemos decidido usar el lenguaje JPQL(Java Persistence Query Languague).
	// ':emailVerificationStatus' y ':userId' hacen referencia a los valores de los argumentos de entrada del método "emailVerificationStatus" y "userId" respectivamente
	// En este caso, indicar la anotación @Param es opcional porque los nombres de ámbos parámetros de la sentencia JPQL coinciden con los nombres de ámbos argumentos de entrada del método
	@Transactional
	@Modifying // Como esta consulta JPQL modifica un registro en la tabla correspondiente de la base de datos y no es una consulta, tenemos que indicar esta anotación 
	@Query("update UserEntity u set u.emailVerificationStatus=:emailVerificationStatus where u.userId=:userId")
	void updateUserEntityEmailVerificationStatus(boolean emailVerificationStatus, String userId);
}
