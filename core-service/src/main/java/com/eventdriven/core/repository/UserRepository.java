package com.eventdriven.core.repository;

import com.eventdriven.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<User> search(String term);
}
