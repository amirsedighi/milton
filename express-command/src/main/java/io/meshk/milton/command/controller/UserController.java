package io.meshk.milton.command.controller;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.hazelcast.query.Predicates;
import io.meshk.milton.command.model.User;
import io.meshk.milton.command.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private HazelcastInstance imdg;

    @Value("${imdg.distributed.map.user:express_user}")
    String distributedUserMap;

    private IMap<Long, User> distributedUsers() {
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
        distributedUsers().put(newUser.getId(), newUser);
        return newUser;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        User newUser = userService.save(user);
        distributedUsers().put(newUser.getId(), newUser);
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

    @GetMapping("/find-imdg/username/{useranme}")
    public Optional<Collection<User>> findByUsernameImdg(@PathVariable String useranme) {
        Predicate predicate = Predicates.sql("username like "+useranme+"%");
        log.info("/find-imdg/username like "+useranme+"%");
        Collection<User> filtered = distributedUsers().values(predicate);
        return Optional.ofNullable(filtered);
    }

//    @GetMapping("/find/birthdate-after/{date}")
//    public List<User> findBirthdateAfter(
//            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
//        return userService.findByPublishedDateAfter(date);
//    }
//
//
//    @GetMapping("/find-imdg/birthdate-after/{date}")
//    public Optional<Collection<User>> findByBirthdateAfterImdg(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
//        PredicateBuilder.EntryObject e = Predicates.newPredicateBuilder().getEntryObject();
//        Predicate predicate = e.get( "publishdate" ).lessThan( );
//        Collection<User> filtered = distributedUsers().values(predicate);
//        return Optional.ofNullable(filtered);
//    }


    @GetMapping("reload")
    public Integer reload() {
        log.info("reloading users is in progress");
        int i = 0;
        for (User user : userService.findAll()){
            distributedUsers().put(user.getId(), user);
            i++;
        }
        return i;
    }

}
