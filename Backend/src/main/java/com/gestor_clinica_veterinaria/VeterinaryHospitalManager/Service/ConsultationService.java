package com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Service;

import com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Dto.Consultation.ConsultationDto;
import com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Dto.Consultation.ConsultationResponseDto;
import com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Entity.*;
import com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Mapper.ConsultationMapper;
import com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final ConsultationMapper consultationMapper;
    private final DiagnosticRepository diagnosticRepository;
    private final ComplementaryStudyRepository complementaryStudyRepository;
    private final PetRepository petRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final InvoiceRepository invoiceRepository;


    @Transactional
    public ConsultationResponseDto addConsultation(@Valid ConsultationDto dto) {
       Veterinarian veterinarian = veterinarianRepository.findById(dto.id_veterinarian())
               .orElseThrow(() -> new EntityNotFoundException("Veterinario no encontrado con ID: " + dto.id_veterinarian()));
       Pet pet = petRepository.findById(dto.id_pet())
               .orElseThrow(() -> new EntityNotFoundException("Mascota no encontrada con ID: " + dto.id_pet()));
       ConsultationEntity entity = consultationMapper.toEntity(dto);
       entity.setVeterinarian(veterinarian);
       entity.setPet(pet);
       ConsultationEntity savedEntity = consultationRepository.save(entity);
       return consultationMapper.toResponseDto(savedEntity);
    }

    @Transactional
    public ConsultationDto updateConsultation(Long id, @Valid ConsultationDto dto) {
        ConsultationEntity consultationEntity = consultationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta no encontrada con ID: " + id));
        try{
            consultationEntity.setName(dto.name());
            consultationEntity.setConsultationDate(dto.consultationDate());
            consultationEntity.setAnamnesis(dto.anamnesis());
            consultationEntity.setObservations(dto.observations());
            consultationEntity.setState(dto.state());
            consultationEntity.setCostConsultation(dto.costConsultation());

            if(dto.id_veterinarian() != null){
                Veterinarian veterinarian = veterinarianRepository.findById(dto.id_veterinarian())
                        .orElseThrow(() -> new EntityNotFoundException("Veterinario no encontrado con ID: " + dto.id_veterinarian()));
                Pet pet = petRepository.findById(dto.id_pet())
                        .orElseThrow(() -> new EntityNotFoundException("Mascota no encontrada con ID: " + dto.id_pet()));
                consultationEntity.setVeterinarian(veterinarian);
                consultationEntity.setPet(pet);
            }

            ConsultationEntity savedEntity = consultationRepository.save(consultationEntity);
            return consultationMapper.toDto(savedEntity);
        }catch (DataIntegrityViolationException e){
            throw new IllegalArgumentException("Error de integridad de datos al actualizar la consulta");
        }catch (Exception e){
            throw new IllegalArgumentException("El id de la mascota o el id del veterinario no existen");
        }
    }

    @Transactional(readOnly = true)
    public ConsultationResponseDto getConsultationById(Long id) {
        ConsultationEntity consultationEntity = consultationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consulta no encontrada con ID: " + id));
        return consultationMapper.toResponseDto(consultationEntity);
    }

    @Transactional(readOnly = true)
    public Page<ConsultationResponseDto> getAllConsultations(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid page or size parameters");
        }
        Page<ConsultationEntity> consultationPage = consultationRepository.findAll(PageRequest.of(page, size, Sort.by("id")));
        return consultationPage.map(consultationMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<ConsultationResponseDto> searchConsultations(int page, int size, String query) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid page or size parameters");
        }
        try{
            Page<ConsultationEntity> consultationPage = consultationRepository.findByNameContainingIgnoreCase(query, PageRequest.of(page, size, Sort.by("name")));
            return consultationPage.map(consultationMapper::toResponseDto);
        }catch (Exception e){
            throw new IllegalArgumentException("La consulta con el nombre: " + query + " no existe");
        }
    }

    @Transactional(readOnly = true)
    public Page<ConsultationResponseDto> searchConsultationsByPetName(int page, int size, String petName) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid page or size parameters");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ConsultationEntity> consultationPage = consultationRepository.findByPet_NameContainingIgnoreCase(petName, pageable);
        return consultationPage.map(consultationMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public Page<ConsultationResponseDto> searchConsultationsByOwnerName(int page, int size, String query) {
       if(page < 0 || size <= 0){
           throw new IllegalArgumentException("Invalid page or size parameters");
       }
       Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
       Page<ConsultationEntity> consultationPage = consultationRepository.findByPet_Owner_NameContainingIgnoreCase(query, pageable);
       return consultationPage.map(consultationMapper::toResponseDto);

    }

    @Transactional(readOnly = true)
    public ConsultationResponseDto getConsultationByDiagnosisId(Long diagnosisId) {
        DiagnosticEntity diagnosis = diagnosticRepository.findById(diagnosisId)
                .orElseThrow(() -> new EntityNotFoundException("Diagnostico no encontrado con ID: " + diagnosisId));

        ConsultationEntity consultation = diagnosis.getConsultation();
        return consultationMapper.toResponseDto(consultation);
    }

    @Transactional(readOnly = true)
    public ConsultationResponseDto getConsultationByComplementaryStudyId(Long complementaryStudyId) {
        ComplementaryStudy complementaryStudy = complementaryStudyRepository.findById(complementaryStudyId)
                .orElseThrow(() -> new EntityNotFoundException("Estudio complementario no encontrado con ID: " + complementaryStudyId));

        ConsultationEntity consultation = complementaryStudy.getConsultation();
        return consultationMapper.toResponseDto(consultation);
    }

    @Transactional(readOnly = true)
    public Page<ConsultationResponseDto> getConsultationsByPetId(int page, int size, Long petId) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid page or size parameters");
        }
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
            Page<ConsultationEntity> consultationPage = consultationRepository.findByPetId(petId, pageable);
            return consultationPage.map(consultationMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public  Page<ConsultationResponseDto> getConsultationsByVeterinaryId(int page, int size, Long vetId) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid page or size parameters");
        }
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
            Page<ConsultationEntity> consultationPage = consultationRepository.findByVeterinarian(vetId, pageable);
            return consultationPage.map(consultationMapper::toResponseDto);

    }

    @Transactional
    public void deleteConsultation(Long id) {
        try {
            // Buscar la consulta por id
            ConsultationEntity consultation = consultationRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("La consulta no existe con id: " + id));

            // Verificar si hay una factura asociada
            InvoiceEntity invoice = consultation.getInvoice();
            if (invoice != null) {
                // Desvincular la factura de la consulta
                invoice.setConsultation(null);
                invoiceRepository.save(invoice);  // Guardar los cambios en la factura
            }

            // Eliminar la consulta
            consultationRepository.deleteById(id);

        } catch (EmptyResultDataAccessException ex) {
            throw new EntityNotFoundException("La consulta no se puede eliminar porque no existe: " + ex.getMessage());
        }
    }

}
