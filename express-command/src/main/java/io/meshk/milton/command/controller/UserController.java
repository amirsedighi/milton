package io.meshk.milton.command.controller;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import io.meshk.milton.command.model.Permission;
import io.meshk.milton.command.model.User;
import io.meshk.milton.command.service.datastore.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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


    @Value("${imdg.distributed.map.user-permissions:express_user_permissions}")
    String distributedUserPermissionsMap;


    private IMap<Long, Set<Long>> distributedUserPermissions() {
        return imdg.getMap(distributedUserPermissionsMap);
    }


    @Value("${imdg.distributed.map.permission:express}")
    String distributedPermissionMap;

    private IMap<Long,Permission> disributedPermissions() {
        return imdg.getMap(distributedPermissionMap);
    }


    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<User> findById(@PathVariable Long id) {
        log.info("---------------- findById  called: {}", String.valueOf(id));
        return userService.findById(id);
    }

    @GetMapping("/imdg/{id}")
    public Optional<User> findByIdByIMDG(@PathVariable Long id) {
        log.info("---------------- findByIdByIMDG  called: {}", String.valueOf(id));
        return Optional.ofNullable(distributedUsers().get(id));
    }


    @GetMapping("/imdg/deep/{id}")
    public Optional<User> findByIdByIMDGDeep(@PathVariable Long id) {
        log.info("---------------- findByIdByIMDGDeep, called: {}", String.valueOf(id));
        User user = distributedUsers().get(id);
        if (distributedUserPermissions().get(id)==null || distributedUserPermissions().get(id).isEmpty()){
            return Optional.ofNullable(user);
        } else {
            Set<Permission> perms = new HashSet<>();
            for (Long permId : distributedUserPermissions().get(id)){
                perms.add(disributedPermissions().get(permId));
            }
            user.setPermissions(new HashSet<>(perms));
        }
        return Optional.ofNullable(user);
    }



    @ResponseStatus(HttpStatus.CREATED) // 201
    @PostMapping
    public User create(@RequestBody User user) {
        User newUser = userService.save(user);
        log.info("user created - id: {}", String.valueOf(newUser.getId()));
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

        log.info("/find/username/{}", username);

        return userService.findByUsername(username);
    }

    @GetMapping("/imdg/find/username/{useranme}")
    public Optional<Collection<User>> findByUsernameImdg(@PathVariable String useranme) {
        Predicate predicate = Predicates.sql("username like %"+useranme+"%");
        log.info("/imdg/find/username/ like %"+useranme+"%");
        Collection<User> filtered = distributedUsers().values(predicate);
        return Optional.ofNullable(filtered);
    }

    @GetMapping("/imdg/deep/find/username/{useranme}")
    public Optional<Collection<User>> findByUsernameImdgDeep(@PathVariable String useranme) {
        Predicate predicate = Predicates.sql("username like %"+useranme+"%");
        log.info("/imdg/deep/find/username like %"+useranme+"%");

        Collection<User> filtered = distributedUsers().values(predicate);

        Collection<User> resultSet = new HashSet<>();
        for (User filteredUser : filtered){
            if (distributedUserPermissions().get(filteredUser.getId())!=null && !distributedUserPermissions().get(filteredUser.getId()).isEmpty()){
                Set<Permission> perms = new HashSet<>();
                for (Long permId : distributedUserPermissions().get(filteredUser.getId())){
                    perms.add(disributedPermissions().get(permId));
                }
                filteredUser.setPermissions(new HashSet<>(perms));
            }
            resultSet.add(filteredUser);

        }
        return Optional.ofNullable(resultSet);
    }

    @GetMapping("/imdg/deepp/find/username/{useranme}")
    public Optional<Collection<User>> findByUsernameImdgDeepParallel(@PathVariable String useranme) {
        Predicate predicate = Predicates.sql("username like %"+useranme+"%");
        log.info("/imdg/deepp/find/username like %"+useranme+"%");

        Collection<User> filtered = distributedUsers().values(predicate);

        Collection<User> resultSet = new HashSet<>();

        filtered.parallelStream().forEach((filteredUser) -> {
            if (distributedUserPermissions().get(filteredUser.getId())!=null && !distributedUserPermissions().get(filteredUser.getId()).isEmpty()){
                Set<Permission> perms = new HashSet<>();
                for (Long permId : distributedUserPermissions().get(filteredUser.getId())){
                    perms.add(disributedPermissions().get(permId));
                }
                filteredUser.setPermissions(new HashSet<>(perms));
            }
            resultSet.add(filteredUser);
        });

        return Optional.ofNullable(resultSet);
    }


    @GetMapping("/imdg/deepp2/find/username/{useranme}")
    public Optional<Collection<User>> findByUsernameImdgDeepParallel2(@PathVariable String useranme) {
        Predicate predicate = Predicates.sql("username like %"+useranme+"%");
        log.info("/imdg/deepp2/find/username like %"+useranme+"%");

        Collection<User> filtered = new HashSet<>();

        Collection<User> resultSet = new HashSet<>();

        imdg.getMap(distributedUserMap).entrySet().parallelStream().forEach((user) -> {
            if (((User)user.getValue()).getUsername().contains(useranme)){
                if (distributedUserPermissions().get(((User)user.getValue()).getId())!=null && !distributedUserPermissions().get(((User)user.getValue()).getId()).isEmpty()){
                    Set<Permission> perms = new HashSet<>();
                    for (Long permId : distributedUserPermissions().get(((User)user.getValue()).getId())){
                        perms.add(disributedPermissions().get(permId));
                    }
                    ((User)user.getValue()).setPermissions(new HashSet<>(perms));
                }
                resultSet.add((User)user.getValue());

            }
        });


        return Optional.ofNullable(resultSet);
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
        userService.findAll().parallelStream().forEach(user -> {
            Set<Long> usersPermissions = new HashSet();
            for (Permission permission : user.getPermissions()){
                usersPermissions.add(permission.getId());
            }
            distributedUsers().put(user.getId(), user);
            distributedUserPermissions().put(user.getId(), usersPermissions);
        });
        return 0;
    }

}
