package io.meshk.milton.b;

import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BetaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BetaApplication.class, args);
	}

	@Bean
	public JaegerGrpcSpanExporter otelJaegerGrpcSpanExporter(@Value("${tracing.url}") String url) {
		return JaegerGrpcSpanExporter.builder().setEndpoint(url).build();
	}


}
