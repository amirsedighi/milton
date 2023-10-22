package io.meshk.milton.b.stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component

public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);


    @KafkaListener(topics = "${spring.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void
    consume(String message)
    {

        logger.info("{} received!", message);

    }
}