package com.example.vaccineManagement.Services;

import com.example.vaccineManagement.Entity.Doctor;
import com.example.vaccineManagement.Entity.Vaccine;
import com.example.vaccineManagement.Exceptions.DoctorNotFound;
import com.example.vaccineManagement.Exceptions.VaccineNotFound;
import com.example.vaccineManagement.Repository.DoctorRepository;
import com.example.vaccineManagement.Repository.VaccineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VaccineService {

    @Autowired
    private VaccineRepository vaccineRepository;

    @Autowired
    private DoctorRepository doctorRepository;
    
    public String addVaccine(Vaccine vaccine) {
        vaccineRepository.save(vaccine);
        return "Vaccine added successfully!";
    }

    public String associateVaccineWithDoctor(int vaccineId, int doctorId)
            throws VaccineNotFound, DoctorNotFound {

        Optional<Vaccine> vaccineOpt = vaccineRepository.findById(vaccineId);
        if (!vaccineOpt.isPresent()) {
            throw new VaccineNotFound("Vaccine not found with id: " + vaccineId);
        }

        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (!doctorOpt.isPresent()) {
            throw new DoctorNotFound("Doctor not found with id: " + doctorId);
        }

        Vaccine vaccine = vaccineOpt.get();
        Doctor doctor = doctorOpt.get();

        vaccine.setDoctor(doctor);                 
        doctor.getVaccineList().add(vaccine);        
        vaccineRepository.save(vaccine);             
                
        return "Vaccine '" + vaccine.getVaccineName() + "' linked with Doctor: " + doctor.getName();
    }

    // Get all vaccines
    public List<Vaccine> getAllVaccines() {
        return vaccineRepository.findAll();
    }

    //Get vaccine by ID
    public Vaccine getVaccineById(int id) throws VaccineNotFound {
        return vaccineRepository.findById(id)
                .orElseThrow(() -> new VaccineNotFound("Vaccine not found with id: " + id));
    }

    public List<Vaccine> getVaccinesByDoctorId(int doctorId) {
        return vaccineRepository.findByDoctor_DocId(doctorId);
    }
}
