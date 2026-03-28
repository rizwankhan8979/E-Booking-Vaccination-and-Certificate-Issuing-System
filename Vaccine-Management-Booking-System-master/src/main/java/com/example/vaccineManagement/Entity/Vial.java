package com.example.vaccineManagement.Entity;

import com.example.vaccineManagement.Enums.VialStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vials")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true, nullable = false)
    private String vialNumber; // Unique Tracking QR/Barcode No.

    private int remainingDoses;

    @Enumerated(EnumType.STRING)
    private VialStatus status = VialStatus.AVAILABLE;

    // Many vials belong to one Vaccine (Batch)
    @ManyToOne
    @JoinColumn(name = "vaccine_id") // Foreign key column
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties("vials")
    private Vaccine vaccine;
}
