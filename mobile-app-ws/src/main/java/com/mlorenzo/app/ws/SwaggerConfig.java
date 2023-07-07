package com.mlorenzo.app.ws;

import java.util.ArrayList;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {
	private Contact contact = new Contact("Manuel Lorenzo", "http://www.test.com", "test@test.com");
	private ApiInfo apiInfo = new ApiInfo("Photo app RESTful Web Service documentation",
			"This pages documents Photo app RESTful Web Service endpoints", "1.0",
			"http://test.com/service.html", contact, "Apache 2.0",
			"http://www.apache.org/licenses/LICENSE-2.0", new ArrayList<>());
	
	@Bean
	public Docket apiDocket() {
		Docket docket = new Docket(DocumentationType.SWAGGER_2)
				.protocols(Set.of("HTTP", "HTTPS"))
				.apiInfo(apiInfo)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.mlorenzo.app.ws"))
				.paths(PathSelectors.any())
				.build();
		return docket;
	}
}
