package com.gaejangmo.apiserver.model.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByOauthId(Long oauthId);
    Optional<User> findByUsername(String username);
    List<User> findAllByUsernameContainingIgnoreCase(String username);
}
