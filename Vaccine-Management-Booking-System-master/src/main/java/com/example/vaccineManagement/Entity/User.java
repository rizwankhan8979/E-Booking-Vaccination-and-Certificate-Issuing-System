package com.example.vaccineManagement.Entity;
import com.example.vaccineManagement.Enums.Gender;
import com.example.vaccineManagement.Enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(name = "user_name")
    private String name;

    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String mobileNo;

<<<<<<< HEAD
=======
    //check dose status
    private boolean isVaccinated = false;

    // 🔗 Link to AuthUser
>>>>>>> b4f768d (Updated backend After Create Apis)
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "auth_user_id", nullable = false)
    private AuthUser authUser;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Dose> doseList = new ArrayList<>();

    @JsonIgnore
   @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Appointment> appointmentList = new ArrayList<>();


}
