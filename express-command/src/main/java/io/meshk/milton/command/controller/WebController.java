package io.meshk.milton.command.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.ConcurrentMap;

@RestController
public class WebController {
    @Autowired
    private HazelcastInstance imdg;


    String distributedMap="test";
    private ConcurrentMap<String,String> permission() {
        return imdg.getMap(distributedMap);
    }

    @GetMapping("/put")
    public void put(@RequestParam(value = "key") String key, @RequestParam(value = "value") String value) {
        permission().put(key, value);
    }

    @GetMapping("/get")
    public String get(@RequestParam(value = "key") String key) {
        String value = permission().get(key);
        return value;
    }
}