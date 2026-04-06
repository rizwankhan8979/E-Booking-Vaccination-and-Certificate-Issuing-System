package com.example.vaccineManagement.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "vaccines")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String vaccineName;
    private String manufacturer;
    private int dosesRequired;
    private String ageRange;
    private double price;

    @Column(unique = true, nullable = false)
    private String batchNumber; // Pure batch ka common ID

    //total Doses capacity
    private int totalBatchCapacity;

    // set vial capacity per bottle
    private int dosesPerVial;

    //Manual Entry Manufacturing data
    @JsonFormat(pattern = "yyyy-MM-dd")
    private java.util.Date entryDate;

    //manually enter Expire date of Vaccine
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expiryDate;


    // Default to "Active"
    private String status = "Active";

    // Many vaccines belong to one doctor
    @ManyToOne
    @JoinColumn(name = "doctor_id") // foreign key
    @JsonIgnore
    private Doctor doctor;

    // One Vaccine (Batch) has many Vials
    @OneToMany(mappedBy = "vaccine", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private java.util.List<Vial> vials = new java.util.ArrayList<>();
}
