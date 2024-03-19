package com.iaa.camelkafkademoconsumer;

import org.apache.camel.opentelemetry.starter.CamelOpenTelemetry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;

@SpringBootApplication
@CamelOpenTelemetry
public class CamelKafkaDemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(CamelKafkaDemoApplication.class, args);	
	}

	@Bean
    public OpenTelemetry openTelemetry() {
		return AutoConfiguredOpenTelemetrySdk.initialize().getOpenTelemetrySdk();
	}
}
