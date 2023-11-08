package io.meshk.milton.command.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/control")
@Slf4j
public class ControlController {

    @Autowired
    UserController userController;

    @Autowired
    PermissionController permissionController;

    @GetMapping("/reloadall")
    public void get() {
        log.info("reloading all ...");
        userController.reload();
        permissionController.reload();
        log.info("reloading finished.");
    }
}