package io.meshk.milton.b.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController // marks the class as a controller
@RequestMapping("/beta") // Root path to the API endpoints in this class
public class HelloController {

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping(value = "/hello")// Get request that returns all students
    public ResponseEntity<String> getAll() throws InterruptedException {
        logger.info("beta looks good");
        return new ResponseEntity<>("Hello from B!", HttpStatus.OK);
    }


}