package io.meshk.milton.command.controller;

import com.hazelcast.core.HazelcastInstance;
import io.meshk.milton.command.model.Permission;
import io.meshk.milton.command.service.PermissionService;
import io.meshk.milton.command.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

@RestController
@RequestMapping("/permissions")
@Slf4j
public class PermissionController {

    @Autowired
    private HazelcastInstance imdg;

    @Value("${imdg.distributed.map.permission:express}")
    String distributedPermissionMap;

    private ConcurrentMap<Long,Permission> disributedPermissions() {
        return imdg.getMap(distributedPermissionMap);
    }

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<Permission> findAll() {
        return permissionService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Permission> findById(@PathVariable Long id) {
        log.info("DATABASE findById  called: {}", String.valueOf(id));
        return permissionService.findById(id);
    }

    @GetMapping("/imdg/{id}")
    public Optional<Permission> findByIdByIMDG(@PathVariable Long id) {
        log.info("IMDG findById  called: {}", String.valueOf(id));
        return Optional.ofNullable(disributedPermissions().get(id));
    }


    @ResponseStatus(HttpStatus.CREATED) // 201
    @PostMapping
    public Permission create(@RequestBody Permission permission) {
        Permission newPermission = permissionService.save(permission);
        log.info("permission created - id: {}", String.valueOf(newPermission.getId()));
        disributedPermissions().put(newPermission.getId(), permission);
        return newPermission;
    }

    @PutMapping
    public Permission update(@RequestBody Permission permission) {
        log.info("update called id: {}", String.valueOf(permission.getId()));
        Permission newPermission = permissionService.save(permission);
        disributedPermissions().put(newPermission.getId(), permission);
        return newPermission;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        permissionService.deleteById(id);
        disributedPermissions().remove(id);
    }

    @GetMapping("/find/title/{title}")
    public List<Permission> findByTitle(@PathVariable String title) {
        return permissionService.findByTitle(title);
    }

    @GetMapping("/find/date-after/{date}")
    public List<Permission> findByPublishedDateAfter(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return permissionService.findByPublishedDateAfter(date);
    }

}