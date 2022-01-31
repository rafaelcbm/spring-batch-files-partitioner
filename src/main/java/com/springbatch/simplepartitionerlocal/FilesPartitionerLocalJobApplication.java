package com.springbatch.simplepartitionerlocal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FilesPartitionerLocalJobApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(FilesPartitionerLocalJobApplication.class, args);
		context.close();
	}

}
