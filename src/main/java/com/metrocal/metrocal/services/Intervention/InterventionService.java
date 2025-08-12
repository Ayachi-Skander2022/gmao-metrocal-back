package com.metrocal.metrocal.services.Intervention;

import java.io.IOException;
import java.util.List;

import com.metrocal.metrocal.dto.InterventionRequestDto;
import com.metrocal.metrocal.dto.InterventionResponseDto;

public interface InterventionService {

   InterventionResponseDto createIntervention(Long demandeId, InterventionRequestDto dto) throws IOException ;

      List<InterventionResponseDto> getInterventionsByTechnicien(Long techId) ;

      List<InterventionResponseDto> getInterventionsByClient(Long clientId);

      List<InterventionResponseDto> getAllInterventions();

    
}
