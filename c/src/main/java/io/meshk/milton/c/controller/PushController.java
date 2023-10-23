package io.meshk.milton.c.controller;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PushController {

    private static final Logger logger = LoggerFactory.getLogger(PushController.class);

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;


    @Value("${spring.kafka.topic}")
    String TOPIC;

    @PostConstruct
    void init() {
        kafkaTemplate.setObservationEnabled(true);
    }

    // Publish messages using the GetMapping
    @GetMapping("/push/{message}")
    public String publishMessage(@PathVariable("message") final String message) {

        logger.info("Just a simple line of log!");
        kafkaTemplate.send(TOPIC, message);

        logger.info("message pushed to topic:{},   message: {}", TOPIC, message);

        return "done";

    }


    @GetMapping(value = "/hello")
    public String process(@RequestParam String message) throws InterruptedException {
        logger.info("processing {} ",message);
        return String.format("processed: {}",message);
    }


}

