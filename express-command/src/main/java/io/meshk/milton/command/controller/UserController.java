package io.meshk.milton.command.controller;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.MapEvent;
import io.meshk.milton.command.model.Permission;
import io.meshk.milton.command.model.User;
import io.meshk.milton.command.service.datastore.UserService;
import jakarta.annotation.PostConstruct;
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

    public Boolean imdgAlreadyInitiated(){
        return !distributedUsers().isEmpty();
    }

    @Value("${imdg.distributed.map.permission:express}")
    String distributedPermissionMap;

    private IMap<Long,Permission> distributedPermissions() {
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
                perms.add(distributedPermissions().get(permId));
            }
            user.setPermissions(new HashSet<>(perms));
        }
        return Optional.ofNullable(user);
    }


    @GetMapping("/imdg/deepp/{id}")
    public Optional<User> findByIdByIMDGDeepParallel(@PathVariable Long id) {
        log.info("---------------- findByIdByIMDGDeepParallel, called: {}", String.valueOf(id));
        User user = distributedUsers().get(id);
        if (distributedUserPermissions().get(id)==null || distributedUserPermissions().get(id).isEmpty()){
            return Optional.ofNullable(user);
        } else {
            Set<Permission> perms = new HashSet<>();
            distributedUserPermissions().get(id)
                    .parallelStream()
                    .forEach((perm) -> {
                        perms.add(distributedPermissions().get(perm));
                    } );

            user.setPermissions(new HashSet<>(perms));

        }

        return Optional.ofNullable(user);
    }

    /*

    ExecutorService executor = Executors.newSingleThreadExecutor();
   Future<String> firstApiCallResult = executor.submit(
           () -> firstApiCall(someValue)
   );

   String stringResult = firstApiCallResult.get();
   Future<String> secondApiCallResult = executor.submit(
           () -> secondApiCall(stringResult)
   );
     */

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


    @GetMapping("/find/username-lname/{username}/{lname}")
    public List<User> findByUsername(@PathVariable String username, @PathVariable String lname) {

        log.info("/find/username-lname/{}/{}", username,lname);

        return userService.findByUsernameAndLname(username,lname);
    }


    @GetMapping("/imdg/find/username/{useranme}")
    public Optional<Collection<User>> findByUsernameImdg(@PathVariable String useranme) {
        Predicate predicate = Predicates.sql("username like %"+useranme+"%");
        log.info("/imdg/find/username/ like %{}%  --- map size:{} ", useranme, distributedUsers().size());
        Collection<User> filtered = distributedUsers().values(predicate);
        return Optional.ofNullable(filtered);
    }

    @GetMapping("/imdg/deep/find/username/{useranme}")
    public Optional<Collection<User>> findByUsernameImdgDeep(@PathVariable String useranme) {
        Predicate predicate = Predicates.sql("username like %"+useranme+"%");
        log.info("/imdg/deep/find/username/ like %{}%  --- map size:{} ", useranme, distributedUsers().size());

        Collection<User> filtered = distributedUsers().values(predicate);

        Collection<User> resultSet = new HashSet<>();
        for (User filteredUser : filtered){
            if (distributedUserPermissions().get(filteredUser.getId())!=null && !distributedUserPermissions().get(filteredUser.getId()).isEmpty()){
                Set<Permission> perms = new HashSet<>();
                for (Long permId : distributedUserPermissions().get(filteredUser.getId())){
                    perms.add(distributedPermissions().get(permId));
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
        log.info("/imdg/deepp/find/username like %{}%  --- map size:{} ", useranme, distributedUsers().size());

        Collection<User> filtered = distributedUsers().values(predicate);

        Collection<User> resultSet = new HashSet<>();

        filtered.parallelStream().forEach((filteredUser) -> {
            if (distributedUserPermissions().get(filteredUser.getId())!=null && !distributedUserPermissions().get(filteredUser.getId()).isEmpty()){
                Set<Permission> perms = new HashSet<>();
                for (Long permId : distributedUserPermissions().get(filteredUser.getId())){
                    perms.add(distributedPermissions().get(permId));
                }
                filteredUser.setPermissions(new HashSet<>(perms));
            }
            resultSet.add(filteredUser);
        });

        return Optional.ofNullable(resultSet);
    }



    @GetMapping("/imdg/deepp/find/username-lname/{useranme}/{lname}")
    public Optional<Collection<User>> findByUsernameAndLnameImdgDeepParallel(@PathVariable String useranme, @PathVariable String lname) {
        Predicate predicate = Predicates.sql("username like %"+useranme+"% AND lname like %"+lname+"%");
        log.info("/imdg/deepp/find/username like %{}% AND lname like %{}% --- map size:{} ", useranme, lname, distributedUsers().size());

        Collection<User> filtered = distributedUsers().values(predicate);

        Collection<User> resultSet = new HashSet<>();

        filtered.parallelStream().forEach((filteredUser) -> {
            if (distributedUserPermissions().get(filteredUser.getId())!=null && !distributedUserPermissions().get(filteredUser.getId()).isEmpty()){
                Set<Permission> perms = new HashSet<>();
                for (Long permId : distributedUserPermissions().get(filteredUser.getId())){
                    perms.add(distributedPermissions().get(permId));
                }
                filteredUser.setPermissions(new HashSet<>(perms));
            }
            resultSet.add(filteredUser);
        });

        return Optional.ofNullable(resultSet);
    }

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

        distributedUsers().addEntryListener( new UserEntryListener(), true );
        log.info("User Listener Registered :)");

        return 0;
    }


    private static class UserEntryListener implements EntryListener<Long, User> {

        @Override
        public void entryAdded(EntryEvent<Long, User> event) {
            log.info("User Added: id{}, username: {}, ", event.getValue().getId(), event.getValue().getUsername());
        }

        @Override
        public void entryRemoved(EntryEvent<Long, User>  event) {
            log.info("User Removed: id{}, username: {}, ", event.getValue().getId(), event.getValue().getUsername());
        }

        @Override
        public void entryUpdated(EntryEvent<Long, User> event) {
            log.info("User Updated: id{}, username: {}, ", event.getValue().getId(), event.getValue().getUsername());
        }

        @Override
        public void entryEvicted(EntryEvent<Long, User> event) {
            log.info("User Evicted: id{}, username: {}, ", event.getValue().getId(), event.getValue().getUsername());
        }

        @Override
        public void mapEvicted(MapEvent event) {
            log.info("mapEvicted:" + event);

        }

        @Override
        public void mapCleared(MapEvent event) {
            log.info("mapCleared: " + event);
        }

        @Override
        public void entryExpired(EntryEvent<Long, User> event) {
            log.info("entryExpired:" + event);
        }
    }
}
