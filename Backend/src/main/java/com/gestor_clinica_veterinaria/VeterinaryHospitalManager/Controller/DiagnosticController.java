package com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Controller;

import com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Dto.Diagnosis.DiagnosticDto;
import com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Dto.Diagnosis.DiagnosticResponseDto;
import com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Entity.DiagnosticEntity;
import com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Exceptions.DiagnosticNotFoundException;
import com.gestor_clinica_veterinaria.VeterinaryHospitalManager.Service.DiagnosticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diagnosis")
@RequiredArgsConstructor
@Tag(name = "Diagnosis", description = "Endpoints for managing diagnosis")
@Slf4j
@Validated
public class DiagnosticController {

    private final DiagnosticService diagnosticService;

    @PostMapping("/add")
    @Operation(
            summary = "Add a new diagnosis",
            description = "Creates a new diagnosis entry in the system.",
            tags = {"Diagnosis"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "diagnosis data to be added",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DiagnosticDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "diagnosis created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DiagnosticDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid diagnosis data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Data conflict (e.g., duplicate entry)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> addDiagnostic(@Valid @RequestBody DiagnosticDto dto) {
        try {
            DiagnosticResponseDto diagnosticDto = diagnosticService.addDiagnostic(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(diagnosticDto);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/all")
    @Operation(
            summary = "Retrieve paginated diagnosis",
            description = "Fetch a paginated list of diagnosis based on the provided page and size parameters.",
            tags = {"Diagnosis"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Page of diagnosis retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = Page.class,
                                            description = "Page object containing a list of DiagnosticDto"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(
                            name = "page",
                            description = "Page number to retrieve (zero-based index)",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "integer", defaultValue = "0")
                    ),
                    @Parameter(
                            name = "size",
                            description = "Number of records per page",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "integer", defaultValue = "10")
                    )
            }
    )
    public ResponseEntity<Page<?>> getAllDiagnostics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<?> diagnostics = diagnosticService.getAllDiagnostics(page, size);
        return ResponseEntity.ok(diagnostics);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Retrieve diagnosis by ID",
            description = "Fetch a diagnosis by its unique identifier (ID).",
            tags = {"Diagnosis"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "diagnosis retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DiagnosticDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "diagnosis not found with the provided ID",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ID supplied",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error.class)
                            )
                    )
            }
    )
    public ResponseEntity<DiagnosticDto> getDiagnosticById(@PathVariable Long id) {
        try {
            DiagnosticDto diagnostic = diagnosticService.getDiagnosticById(id);
            return ResponseEntity.ok(diagnostic);
        } catch (DiagnosticNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @GetMapping("/consultation/{consultationId}")
    @Operation(
            summary = "Retrieve diagnosis by consultation ID",
            description = "Fetch a list of diagnosis associated with a specific consultation.",
            tags = {"Diagnosis"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of diagnosis retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class,
                                            description = "Page object containing a list of DiagnosticDto",
                                            example = "List of diagnosis retrieved successfully"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(
                            name = "page",
                            description = "Page number to retrieve (zero-based index)",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "integer", defaultValue = "0")
                    ),
                    @Parameter(
                            name = "size",
                            description = "Number of records per page",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "integer", defaultValue = "10")
                    )
            }
    )
    public ResponseEntity<Page<?>> getDiagnosticsByConsultationId(@RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "10") int size,
                                                                              @PathVariable Long consultationId) {
        return ResponseEntity.ok(diagnosticService.getDiagnosticsByConsultationId(page, size, consultationId));
    }

    @GetMapping("/surgery/{surgeryId}")
    @Operation(
            summary = "Retrieve one diagnosis by surgery ID",
            description = "Retrieve one diagnosis associated with a specific surgery.",
            tags = {"Diagnosis"}
    )
    public ResponseEntity<?> getDiagnosticBySurgeryId(@PathVariable Long surgeryId) {
        return ResponseEntity.ok(diagnosticService.getDiagnosisBySurgeryId(surgeryId));
    }

    @GetMapping("/complementary-study/{complementaryStudyId}")
    @Operation(
            summary = "Retrieve one diagnosis by complementary study ID",
            description = "Retrieve one diagnosis associated with a specific complementary study.",
            tags = {"Diagnosis"}
    )
    public ResponseEntity<?> getDiagnosisByComplementaryStudyId(@PathVariable Long complementaryStudyId) {
        return ResponseEntity.ok(diagnosticService.getDiagnosisByComplementaryStudyId(complementaryStudyId));
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search diagnosis",
            description = "Search for diagnosis based on a query string.",
            tags = {"Diagnosis"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Page of diagnosis retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class,
                                            description = "Page object containing a list of DiagnosticDto",
                                            example = "Page of diagnosis retrieved successfully"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid query string",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error.class)
                            )
                    )
            },
            parameters = {
                    @Parameter(
                            name = "query",
                            description = "Query string to search for",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string")
                    )
            }
    )
    public ResponseEntity<Page<?>> searchDiagnostics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String query
    ) {

        Page<?> diagnostics = diagnosticService.searchDiagnostics(page, size, query);
        return ResponseEntity.ok(diagnostics);
    }

    @PutMapping("/update/{id}")
    @Operation(
            summary = "Update an existing diagnosis",
            description = "Modify the details of an existing diagnosis identified by its ID.",
            tags = {"Diagnosis"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "diagnosis data to be updated",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DiagnosticDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "diagnosis updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DiagnosticDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "diagnosis not found with the provided ID",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ID or diagnosis data supplied",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error.class)
                            )
                    )
            }
    )
    public ResponseEntity<DiagnosticDto> updateDiagnostic(
            @PathVariable Long id,
            @Valid @RequestBody DiagnosticDto dto) {
        try {
            DiagnosticDto updatedDiagnostic = diagnosticService.updateDiagnostic(id, dto);
            return ResponseEntity.ok(updatedDiagnostic);
        } catch (DiagnosticNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Delete a diagnosis",
            description = "Remove a diagnosis from the system by its unique identifier (ID).",
            tags = {"Diagnosis"},
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Diagnostic deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "diagnosis not found with the provided ID",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ID supplied",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error.class)
                            )
                    )
            }
    )
    public ResponseEntity<Void> deleteDiagnostic(@PathVariable Long id) {
        try {
            diagnosticService.deleteDiagnostic(id);
            return ResponseEntity.noContent().build();
        } catch (DiagnosticNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //Obtener diagnostico por consulta id
    /*@GetMapping("/consultation/{consultationId}")
    public ResponseEntity<Page<DiagnosticDto> getDiagnosisByConsultationId(@PathVariable Long consultationId) {
        return diagnosticService.getDiagnosisByConsultationId(consultationId);
    }*/

    //Obtener diagnostico por tratamiento id
    @GetMapping("/treatment/{treatmentId}")
    @Operation(
            summary = "Get Diagnosis by Treatment ID",
            description = "Retrieve the diagnosis associated with a specific treatment ID.",
            tags = {"Diagnosis"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Diagnosis found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DiagnosticDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Diagnosis not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Error.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> getDiagnosisByTreatmentId(@PathVariable Long treatmentId) {
        try{
            DiagnosticResponseDto diagnostics = diagnosticService.getDiagnosisByTreatmentId(treatmentId);
            return ResponseEntity.ok(diagnostics);
        } catch (DiagnosticNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<DiagnosticResponseDto>> getDiagnosticsByPetId(@PathVariable Long petId) {
        List<DiagnosticResponseDto> diagnostics = diagnosticService.getDiagnosticsByPetId(petId);
        if (diagnostics.isEmpty()) {
            return ResponseEntity.noContent().build(); // Si no hay diagnósticos
        }
        return ResponseEntity.ok(diagnostics); // Devuelve la lista de DTOs
    }

}
