package com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Repository;

import com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Entity.Owner;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

  boolean existsByEmail(String email);

  boolean existsByPhone(String phone);

  List<Owner> findAllByNameContainingIgnoreCase(String name);

  List<Owner> findAllByLastnameContainingIgnoreCase(String lastname);
}
