package io.meshk.milton.command.controller;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import io.meshk.milton.command.model.Permission;
import io.meshk.milton.command.model.User;
import io.meshk.milton.command.service.datastore.PermissionService;
import io.meshk.milton.command.service.datastore.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

    @Value("${imdg.distributed.map.user-permissions:express_user_permissions}")
    String distributedUserPermissionsMap;


    private IMap<Long, Collection<Long>> distributedUserPermissions() {
        return imdg.getMap(distributedUserPermissionsMap);
    }


    @PutMapping
    public Permission grant(@RequestParam Long permissionId, @RequestParam Long userId) {
        Optional<Permission> newPermission = permissionService.findById(permissionId);
        if (newPermission!=null) {
            Optional<User> newUser = userService.findById(userId);
            newUser.get().getPermissions().add(newPermission.get());
            userService.save(newUser.get());
            Collection<Long> perms = distributedUserPermissions().get(userId);
            if (perms==null) {
                perms = new ArrayList<>();
            }
            perms.add(permissionId);
            distributedUserPermissions().set(userId, perms);
            log.info("Permission Granted - userId: {}, permissionId: {}", userId, permissionId);
            log.info("Users {}, permissions: {}", userId,  Arrays.toString(distributedUserPermissions().get(userId).toArray()));
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
                        log.info("IMDG Before revoke Users {}, permissions: {}", userId,  Arrays.toString(distributedUserPermissions().get(userId).toArray()));
                        Collection<Long> perms = distributedUserPermissions().get(userId);
                        perms.remove(permissionId);
                        distributedUserPermissions().set(userId, perms);
                        log.info("IMDG After remove Users {}, permissions: {}", userId,  Arrays.toString(distributedUserPermissions().get(userId).toArray()));
                        userService.save(newUser.get());
                        log.info("Permission Revoked - userId: {}, permissionId: {}", userId, permissionId);
                    }
                }
            }
        }


        return newUser.get();
    }



}
