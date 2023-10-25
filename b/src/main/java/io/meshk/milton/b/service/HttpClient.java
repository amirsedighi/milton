package io.meshk.milton.b.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpClient {

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    @Autowired
    RestTemplate restTemplate;

    @Value("${gamma.url}")
    String gammaUrl;

    public void ingest(String msg)
    {
        msg = msg + "_" + RandomStringUtils.randomNumeric(5).trim();
        String uri = gammaUrl +"/push/" + msg ;
        logger.info("calling {}", uri);
        String result = restTemplate.getForObject(uri, String.class);
        logger.info("result");

    }

    public void process(String msg)
    {
        msg = msg + "_" + RandomStringUtils.randomNumeric(5).trim();
        String uri = gammaUrl +"/hello?" + msg;
        logger.info("calling ... {}", uri);
        String result = restTemplate.getForObject(uri, String.class);
        logger.info(result);

    }

    //Scheduled(fixedDelay = 10000)
    public void background()
    {
        String uri = gammaUrl +"/hello?" + "CRON";
        logger.info("calling ... {}", uri);
        String result = restTemplate.getForObject(uri, String.class);
        logger.info(result);

    }


}
