package org.vetti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vetti.model.Vet;

import java.util.Optional;

@Repository
public interface VetRepository extends JpaRepository<Vet, Long> {

    Optional<Vet> findVetByEmail(String email);

    Optional<Vet> findVetById(Long id);

    Optional<Vet> findVetByCuit(String cuil);

}
