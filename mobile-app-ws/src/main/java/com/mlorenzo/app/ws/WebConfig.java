package com.mlorenzo.app.ws;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Bean de configuración de Spring que configura globalmente CORS
// Nota sobre CORS: 2 o más orígenes se consideran iguales si tienen el mismo protocolo o esquema(http o https), el mismo host y el mismo puerto

// Se comenta porque, como estamos usando Spring Security, ahora la configuración global de CORS se implementa en esa capa de seguridad(ver clase WebSecurity)
// Es decir, esta configuración global de CORS tendría sentido aplicarla si no existiese una capa de seguridad con Spring Security 
//@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// Habilita CORS para todas las rutas de esta API REST, para todos los tipos de métodos http y para todos los orígenes
		registry
			.addMapping("/**") // Si queremos indicar que sólo se aplique CORS a una determinada ruta de la API REST, lo hacemos de esta manera; "/users"
			.allowedMethods("*") // Si queremos indicar tipos de métodos http determinados, lo hacemos de esta manera; "GET, POST, PUT, DELETE"
			.allowedOrigins("*"); // Si queremos especificar determinados orígenes, lo hacemos de esta manera; "http://localhost:8083", "http://localhost:8084"
	}

	
}
