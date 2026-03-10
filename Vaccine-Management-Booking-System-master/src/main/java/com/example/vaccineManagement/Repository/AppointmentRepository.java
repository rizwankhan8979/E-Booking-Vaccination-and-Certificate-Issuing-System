package com.example.vaccineManagement.Repository;

import com.example.vaccineManagement.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment,Integer> {

    List<Appointment> findByUser_UserId(Integer userId);

}
