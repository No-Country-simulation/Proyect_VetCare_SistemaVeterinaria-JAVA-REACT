package com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Entity.Enum.EnumSexPet;
import com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Util.Especie;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Pet {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String race;

  @Column(nullable = false)
  private Especie species = Especie.PERRO;

  @Column(nullable = false)
  private LocalDate birthdate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EnumSexPet sex ;

  @Column(nullable = false)
  private String allergies;

  @Column(nullable = false)
  private Boolean castrated = true;

  @Column(nullable = false)
  private Boolean active = true;

  private String details;

 // @ManyToOne(fetch = FetchType.LAZY, targetEntity = Owner.class)
 // @JoinColumn(name = "owner_id", nullable = false)
 // private Owner owner;



  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  private Owner owner;
}
