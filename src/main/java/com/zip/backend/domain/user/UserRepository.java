package com.zip.backend.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    // SELECT * FROM user WHERE email = ?1
    Optional<User> findByEmail(String email);
    Boolean existsByProviderAndEmail(AuthProvider provider, String email);

    // SELECT * FROM user WHERE provider = ?1 and providerId = ?2
    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
}
