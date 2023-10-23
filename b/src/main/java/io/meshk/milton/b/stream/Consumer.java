package io.meshk.milton.b.stream;
import io.meshk.milton.b.service.HttpClient;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class Consumer {

    @Autowired
    HttpClient client;
    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message)
    {

        logger.info("{} received from Kafka", message);

        client.push(message);
        client.push(message);
        client.process(message);

    }


}