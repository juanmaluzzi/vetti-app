package org.vetti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vetti.model.request.PetRequest;

import java.util.Optional;

public interface PetRepository extends JpaRepository<PetRequest, Long> {

    Optional<PetRequest> findPetById(Long id);
}
