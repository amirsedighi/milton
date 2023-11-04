package io.meshk.milton.command.controller;

import com.hazelcast.core.HazelcastInstance;
import io.meshk.milton.command.model.User;
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
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private HazelcastInstance imdg;

    @Value("${imdg.distributed.map.user:express_user}")
    String distributedUserMap;

    private ConcurrentMap<Long, User> distributedUsers() {
        return imdg.getMap(distributedUserMap);
    }

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<User> findById(@PathVariable Long id) {
        log.warn("---------------- findById  called: {}", String.valueOf(id));
        return userService.findById(id);
    }

    @GetMapping("/imdg/{id}")
    public Optional<User> findByIdByIMDG(@PathVariable Long id) {
        log.warn("---------------- findByIdByIMDG  called: {}", String.valueOf(id));
        return Optional.ofNullable(distributedUsers().get(id));
    }


    @ResponseStatus(HttpStatus.CREATED) // 201
    @PostMapping
    public User create(@RequestBody User user) {
        User newUser = userService.save(user);
        log.warn("user created - id: {}", String.valueOf(newUser.getId()));
        distributedUsers().put(newUser.getId(), user);
        return newUser;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        User newUser = userService.save(user);
        distributedUsers().put(newUser.getId(), user);
        return newUser;
    }


    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        userService.deleteById(id);
        distributedUsers().remove(id);
    }

    @GetMapping("/find/username/{username}")
    public List<User> findByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    @GetMapping("/find/date-after/{date}")
    public List<User> findByPublishedDateAfter(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return userService.findByPublishedDateAfter(date);
    }

}
