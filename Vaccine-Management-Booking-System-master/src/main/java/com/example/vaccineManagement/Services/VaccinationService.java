package com.example.vaccineManagement.Services;

import com.example.vaccineManagement.Exceptions.VaccinationAddressNotFound;
import com.example.vaccineManagement.Entity.VaccinationCenter;
import com.example.vaccineManagement.Repository.VaccinationCenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VaccinationService {

    @Autowired
    VaccinationCenterRepository vaccinationCenterRepository;

    public String addVaccinationCenter(VaccinationCenter vaccinationCenter) throws VaccinationAddressNotFound {

        if (vaccinationCenter.getAddress() == null) {
            throw new VaccinationAddressNotFound("Vaccination Address is Empty");
        }
        //Address Validation
        if (vaccinationCenter.getAddress() == null) {
            throw new VaccinationAddressNotFound("Vaccination Address is Empty");
        }

        //Dose Capacity Range Check
        int capacity = vaccinationCenter.getDoseCapacity();

        if (capacity < 1) {
            throw new RuntimeException("Dose capacity must be valid (greater than 0)!");
        }

        vaccinationCenterRepository.save(vaccinationCenter);

        return "Vaccination center added at a location " + vaccinationCenter.getAddress();
    }

    public List<VaccinationCenter> getAllVaccinationCenters() {
        return vaccinationCenterRepository.findAll();
    }
}
