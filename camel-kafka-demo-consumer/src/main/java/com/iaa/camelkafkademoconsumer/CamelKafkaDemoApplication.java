package com.iaa.camelkafkademoconsumer;

import org.apache.camel.opentelemetry.starter.CamelOpenTelemetry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@CamelOpenTelemetry
public class CamelKafkaDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CamelKafkaDemoApplication.class, args);	
	
	}
}
