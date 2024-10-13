package org.vetti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.vetti.model.Pet;

import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    Optional<Pet> findPetById(Long id);
}
