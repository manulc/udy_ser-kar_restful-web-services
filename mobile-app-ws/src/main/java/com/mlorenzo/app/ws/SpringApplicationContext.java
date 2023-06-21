package com.mlorenzo.app.ws;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

// Esta clase es para poder obtener beans del contexto de Spring desde aquellas clases que no son componentes de Spring.
// Tiene que haber un componente o bean de Spring de esta clase para que Spring pueda inyectar de forma automática su bean
// de tipo ApplicationContext en el método setter "setApplicationContext". Para ello, usamos la clase principal de la
// aplicación(también es una  clase de configuración de Spring) para crear el bean de Spring de esta clase. 

public class SpringApplicationContext implements ApplicationContextAware {
	private static ApplicationContext CONTEXT;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		CONTEXT = applicationContext;
	}
	
	public static Object getBean(String beanName) {
		return CONTEXT.getBean(beanName);
	}

}
