package io.meshk.milton.command.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;


@RestController
@RequestMapping("/control")
@Slf4j
public class ControlController {

    @Value("${imdg.auto-reload.enabled:false}")
    Boolean autoReload;

    @Autowired
    UserController userController;


    @Autowired
    PermissionController permissionController;

    @GetMapping("/reloadall")
    public void reloadAll() {
        log.info("reloading all ...");
        userController.reload();
        permissionController.reload();
        log.info("reloading finished.");

    }


    @PostConstruct
    private void autoReload(){
        if (autoReload){
            if (!userController.imdgAlreadyInitiated()){
                reloadAll();
            }
        }
    }

}