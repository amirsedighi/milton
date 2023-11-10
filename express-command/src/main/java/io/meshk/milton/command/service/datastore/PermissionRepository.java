package io.meshk.milton.command.service.datastore;

import io.meshk.milton.command.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findByTitle(String title);

    // Custom query
    @Query("SELECT p FROM Permission p WHERE p.publishdate > :date")
    List<Permission> findByPublishedDateAfter(@Param("date") LocalDate date);

}
