package io.meshk.milton.command.service.datastore;

import io.meshk.milton.command.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> findByUsername(String username) {
        return userRepository.findByUsernameContaining(username);
    }

    public List<User> findByUsernameAndLname(String username, String lname) {
        return userRepository.findByUsernameContainingAndLnameContaining(username, lname);
    }


    public List<User> findByPublishedDateAfter(LocalDate date) {
        return userRepository.findByBirthdateAfter(date);
    }
}
