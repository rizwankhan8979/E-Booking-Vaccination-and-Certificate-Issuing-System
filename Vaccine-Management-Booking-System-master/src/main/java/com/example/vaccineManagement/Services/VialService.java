package com.example.vaccineManagement.Services;

import com.example.vaccineManagement.Entity.Vaccine;
import com.example.vaccineManagement.Entity.Vial;
import com.example.vaccineManagement.Enums.VialStatus;
import com.example.vaccineManagement.Repository.VaccineRepository;
import com.example.vaccineManagement.Repository.VialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VialService {

    @Autowired
    private VialRepository vialRepository;

    @Autowired
    private VaccineRepository vaccineRepository;

    public String manuallyAddVials(int vaccineId, int numberOfVials) {
        Vaccine vaccine = vaccineRepository.findById(vaccineId)
                .orElseThrow(() -> new RuntimeException("Vaccine batch not found!"));

        List<Vial> newlyAddedVials = new ArrayList<>();

        long existingCount = vialRepository.findByVaccine_Id(vaccineId).size();

        for (int i = 1; i <= numberOfVials; i++) {
            Vial vial = new Vial();
            
            // Generate QR Code
            long currentVialNumber = existingCount + i;
            vial.setVialNumber(vaccine.getBatchNumber() + "-VIAL-" + String.format("%03d", currentVialNumber));
            
            vial.setRemainingDoses(vaccine.getDosesPerVial());
            vial.setStatus(VialStatus.AVAILABLE);
            vial.setVaccine(vaccine);
            
            newlyAddedVials.add(vial);
        }

        vialRepository.saveAll(newlyAddedVials);

        return numberOfVials + " Vials have been manually added to Batch: " + vaccine.getBatchNumber() + 
               ". Their barcodes have been automatically generated.";
    }
}
