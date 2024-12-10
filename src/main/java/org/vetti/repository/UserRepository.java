package org.vetti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vetti.model.request.UserRequest;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserRequest, Long> {

    Optional<UserRequest> findUserByEmail(String email);

    Optional<UserRequest> findUserById(Long id);

    Optional<UserRequest> findUserByDni(String dni);
}
