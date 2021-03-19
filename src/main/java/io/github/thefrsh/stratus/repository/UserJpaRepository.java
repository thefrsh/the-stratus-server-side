package io.github.thefrsh.stratus.repository;

import io.github.thefrsh.stratus.model.User;
import io.vavr.control.Option;

import org.springframework.data.repository.Repository;

public interface UserJpaRepository extends Repository<User, Long> {
    User save(User user);

    Option<User> findById(Long id);

    Option<User> findByUsername(String username);

    Option<User> findByUsernameOrEmail(String username, String email);

    void deleteAll();

    boolean existsById(Long id);

    void flush();
}
