package com.example.vaccineManagement.Repository;

import com.example.vaccineManagement.Entity.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VaccineRepository extends JpaRepository<Vaccine, Integer> {
    List<Vaccine> findByDoctor_DocId(int docId);
    boolean existsByBatchNumber(String batchNumber);
    Optional<Vaccine> findByBatchNumber(String batchNumber);
}
