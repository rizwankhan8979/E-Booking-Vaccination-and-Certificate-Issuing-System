package com.example.vaccineManagement.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "appointments")
@Data
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date appointmentDate;

    private LocalTime appointmentTime;

    @JsonIgnore
    @ManyToOne
    @JoinColumn
    private Doctor doctor;

    @ManyToOne
    @JoinColumn
<<<<<<< HEAD
    private Vaccine vaccine; 
    
=======
    private Vaccine vaccine;

>>>>>>> b4f768d (Updated backend After Create Apis)
    @JsonIgnore
    @ManyToOne
    @JoinColumn
    private User user;

}
