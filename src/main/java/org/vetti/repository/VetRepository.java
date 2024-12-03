package org.vetti.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vetti.model.request.VetRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface VetRepository extends JpaRepository<VetRequest, Long> {

    Optional<VetRequest> findVetByEmail(String email);

    Optional<VetRequest> findVetById(Long id);

    Optional<VetRequest> findVetByCuit(String cuil);

    List<VetRequest> findByStatus(String status);

}
