package com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Table(name = "invoice")
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate invoiceDate;

    @OneToOne(targetEntity = ConsultationEntity.class)
    @JoinColumn(name = "consultation_id", nullable = true)
    private ConsultationEntity consultation;

    private BigDecimal totalCost;

    @Column(name = "veterinarian_name", nullable = false)
    private String veterinarianName;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "pet_name", nullable = false)
    private String petName;

}
