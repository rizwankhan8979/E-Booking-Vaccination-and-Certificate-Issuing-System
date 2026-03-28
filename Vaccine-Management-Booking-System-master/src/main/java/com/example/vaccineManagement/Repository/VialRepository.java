package com.example.vaccineManagement.Repository;

import com.example.vaccineManagement.Entity.Vial;
import com.example.vaccineManagement.Enums.VialStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VialRepository extends JpaRepository<Vial, Integer> {
    Optional<Vial> findByVialNumber(String vialNumber);
    List<Vial> findByVaccine_Id(int vaccineId);
    List<Vial> findByVaccine_IdAndStatus(int vaccineId, VialStatus status);
}
