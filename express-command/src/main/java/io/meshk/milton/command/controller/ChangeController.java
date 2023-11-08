package io.meshk.milton.command.controller;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import io.meshk.milton.command.model.Permission;
import io.meshk.milton.command.model.User;
import io.meshk.milton.command.service.PermissionService;
import io.meshk.milton.command.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/changes")
@Slf4j
public class ChangeController {

    @Autowired
    private HazelcastInstance imdg;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;


    @Value("${imdg.distributed.map.user:express}")
    String distributedUserMap;


    private IMap<Long, User> distributedUsers() {
        return imdg.getMap(distributedUserMap);
    }


    @PutMapping
    public Permission grant(@RequestParam Long permissionId, @RequestParam Long userId) {
        Optional<Permission> newPermission = permissionService.findById(permissionId);
        if (newPermission!=null) {
            Optional<User> newUser = userService.findById(userId);
            newUser.get().getPermissions().add(newPermission.get());
            userService.save(newUser.get());
            distributedUsers().put(userId, newUser.get());
            log.info("Permission Granted - userId: {}, permissionId: {}", userId, permissionId);
        }
        return newPermission.get();
    }

    @DeleteMapping
    public User revoke(@RequestParam Long permissionId, @RequestParam Long userId) {

            Optional<User> newUser = userService.findById(userId);
            if (!newUser.isEmpty()){
                if (!newUser.get().getPermissions().isEmpty()){
                    List<Permission> permissions = new ArrayList<>();
                    permissions.addAll(newUser.get().getPermissions());

                    for (Permission perm : permissions){
                        if (perm.getId().equals(permissionId)) {
                            newUser.get().getPermissions().remove(perm);
                            distributedUsers().remove(userId, permissionId);
                            userService.save(newUser.get());
                            log.info("Permission Revoked - userId: {}, permissionId: {}", userId, permissionId);
                        }
                    }
                }
            }


        return newUser.get();
    }



}
