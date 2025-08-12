package com.metrocal.metrocal.controller.Technicien;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.metrocal.metrocal.dto.DemandeResponseDto;
import com.metrocal.metrocal.dto.InterventionRequestDto;
import com.metrocal.metrocal.dto.InterventionResponseDto;
import com.metrocal.metrocal.services.Demandes.DemandeService;
import com.metrocal.metrocal.services.Intervention.InterventionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tech")
@RequiredArgsConstructor



public class TechController {

           private final DemandeService demandeService;

           private final InterventionService interventionService;


   @GetMapping("/demandes/{technicienId}")
    public ResponseEntity<List<DemandeResponseDto>> getDemandesByTechnicien(@PathVariable Long technicienId) {
        List<DemandeResponseDto> demandes = demandeService.getDemandesByTechnicien(technicienId);
        return ResponseEntity.ok(demandes);
    }



 @PostMapping("/createIntervention/{demandeId}")
    public ResponseEntity<InterventionResponseDto> createIntervention(
            @PathVariable Long demandeId,
            @RequestBody InterventionRequestDto dto) throws IOException {

        InterventionResponseDto response = interventionService.createIntervention(demandeId, dto);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/getInterventionBytechnicien/{id}")
    public ResponseEntity<List<InterventionResponseDto>> getByTechnicien(@PathVariable Long id) {
        return ResponseEntity.ok(interventionService.getInterventionsByTechnicien(id));
    }
    
    
}
