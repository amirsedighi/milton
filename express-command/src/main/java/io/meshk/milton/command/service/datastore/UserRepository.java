package io.meshk.milton.command.service.datastore;

import io.meshk.milton.command.model.Permission;
import io.meshk.milton.command.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByUsernameContaining(String username);

    // Custom query
    @Query("SELECT u FROM User u WHERE u.birthdate > :bdate")
    List<User> findByBirthdateAfter(@Param("bdate") LocalDate bdate);

}

