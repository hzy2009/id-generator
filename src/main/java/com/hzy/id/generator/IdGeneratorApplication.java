package com.hzy.id.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class IdGeneratorApplication {
	
	private static ApplicationContext applicationContext;
	
	@Autowired
	private static void setApplicationContext(ApplicationContext _applicationContext) {
		applicationContext = _applicationContext;
	}
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(IdGeneratorApplication.class, args);
	}

}
