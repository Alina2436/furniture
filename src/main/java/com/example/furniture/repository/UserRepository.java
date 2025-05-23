package com.example.furniture.repository;

import com.example.furniture.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Transactional(readOnly = true)
    Optional<User> findByUsername(final String username);

    @Transactional(readOnly = true)
    @Query("select u from User u left join fetch u.likes left join fetch u.orders where u.username = ?1")
    Optional<User> findByUsernameFetch(final String username);
}