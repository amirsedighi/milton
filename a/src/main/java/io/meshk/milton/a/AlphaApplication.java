package io.meshk.milton.a;

import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AlphaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlphaApplication.class, args);
	}

	@Bean
	public JaegerGrpcSpanExporter otelJaegerGrpcSpanExporter(@Value("${tracing.url}") String url) {
		return JaegerGrpcSpanExporter.builder().setEndpoint(url).build();
	}

        public void main(){
              log.info('This is the logs' );
        }

}
