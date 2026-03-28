package com.example.vaccineManagement.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "dose")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dose {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String doseId; // Unique ID (Auto-generate )

    private int doseNumber; // 1 for Dose-1, 2 for Dose-2

    @Column(nullable = false)
    private String vialSerialNumber;

    @CreationTimestamp
    private Date vaccinationDate;

    @ManyToOne // one user have  multiple doses
    @JoinColumn
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn
    private Vaccine vaccine;

}
