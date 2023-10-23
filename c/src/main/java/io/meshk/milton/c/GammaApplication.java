package io.meshk.milton.c;

import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class GammaApplication {

	public static void main(String[] args) {
		SpringApplication.run(GammaApplication.class, args);
	}

	@Bean
	public JaegerGrpcSpanExporter otelJaegerGrpcSpanExporter(@Value("${tracing.url}") String url) {
		return JaegerGrpcSpanExporter.builder().setEndpoint(url).build();
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

}
