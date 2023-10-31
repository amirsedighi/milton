package io.meshk.milton.query.controller;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentMap;

@RestController
public class WebController {
    @Autowired
    private HazelcastInstance imdg;

    @Value("${imdg.distributed.map.name:express}")
    String distributedMap;

    private ConcurrentMap<String, String> retrieveMap() {
        return imdg.getMap(distributedMap);
    }

    @GetMapping("/put")
    public void put(@RequestParam(value = "key") String key, @RequestParam(value = "value") String value) {
        retrieveMap().put(key, value);
    }

    @GetMapping("/get")
    public String get(@RequestParam(value = "key") String key) {
        String value = retrieveMap().get(key);
        return value;
    }
}
