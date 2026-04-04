package edu.cupk.simple_library_system.repository;

import edu.cupk.simple_library_system.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userName);

    Optional<User> findByUserNameAndUserPasswordAndIsAdmin(String userName, String userPassword, Byte isAdmin);

    Page<User> findByUserNameContaining(String userName, Pageable pageable);

    boolean existsByUserName(String userName);
}
