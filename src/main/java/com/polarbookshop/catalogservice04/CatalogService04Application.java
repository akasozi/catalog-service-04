package com.polarbookshop.catalogservice04;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CatalogService04Application {

	public static void main(String[] args) {
		SpringApplication.run(CatalogService04Application.class, args);
	}

}
