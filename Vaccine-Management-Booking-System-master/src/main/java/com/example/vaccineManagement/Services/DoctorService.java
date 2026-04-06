package com.example.vaccineManagement.Services;

import java.util.List;
import com.example.vaccineManagement.Dtos.RequestDtos.AssociateDocDto;
import com.example.vaccineManagement.Exceptions.CenterNotFound;
import com.example.vaccineManagement.Exceptions.DoctorNotFound;
import com.example.vaccineManagement.Exceptions.EmailIdEmptyException;
import com.example.vaccineManagement.Exceptions.DoctorAlreadyExistsException;
import com.example.vaccineManagement.Entity.Doctor;
import com.example.vaccineManagement.Entity.VaccinationCenter;
import com.example.vaccineManagement.Repository.DoctorRepository;
import com.example.vaccineManagement.Repository.VaccinationCenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private VaccinationCenterRepository centerRepository;

    //Add Doctir data here
    public String addDoctor(Doctor doctor)throws EmailIdEmptyException, DoctorAlreadyExistsException {

        if(doctor.getEmailId()==null){
            throw new EmailIdEmptyException("Email id is mandatory");
        }

        if(doctorRepository.findByEmailId(doctor.getEmailId())!=null){
            throw new DoctorAlreadyExistsException("Doctor with this emailId already exits.");
        }

        doctorRepository.save(doctor);

        return "Doctor has been added to the database";
    }


//functio for Associate doctor with center
    public String associateDoctor(AssociateDocDto associateDocDto)throws DoctorNotFound,
            CenterNotFound {

        Integer docId = associateDocDto.getDocId();

        Optional<Doctor> doctorOptional = doctorRepository.findById(docId);

        if(!doctorOptional.isPresent()){
            throw new DoctorNotFound("Doctor id is wrong");
        }

        Integer centerId = associateDocDto.getCenterId();

        Optional<VaccinationCenter> optionalCenter = centerRepository.findById(centerId);

        if(!optionalCenter.isPresent()){
            throw new CenterNotFound("Center Id entered is incorrect");
        }

        Doctor doctor = doctorOptional.get();
        VaccinationCenter vaccinationCenter = optionalCenter.get();
        doctor.setVaccinationCenter(vaccinationCenter);
        vaccinationCenter.getDoctorList().add(doctor);
        centerRepository.save(vaccinationCenter);
        return "Doctor has been associated to center";

    }

    //fetch all Doctor for display on the Deshboard
    public List<Doctor> getAllDoctors(){
        List<Doctor> doctorList = doctorRepository.findAll();
        return doctorList;
    }

}
