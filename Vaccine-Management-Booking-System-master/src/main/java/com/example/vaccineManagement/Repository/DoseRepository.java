package com.example.vaccineManagement.Repository;


import com.example.vaccineManagement.Entity.Dose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoseRepository extends JpaRepository<Dose,Integer> {
    long countByVialSerialNumber(String vialSerialNumber);
    long countByVaccine_BatchNumber(String batchNumber);
}
