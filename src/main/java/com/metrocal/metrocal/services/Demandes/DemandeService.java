package com.metrocal.metrocal.services.Demandes;

import java.io.IOException;
import java.util.List;

import com.metrocal.metrocal.dto.DemandeRequestDto;
import com.metrocal.metrocal.dto.DemandeResClientDto;
import com.metrocal.metrocal.dto.DemandeResponseDto;
import com.metrocal.metrocal.entities.StatutDemande;

public interface DemandeService {
    
  DemandeResponseDto creerDemande(DemandeRequestDto demandeDto, Long instrumentId) throws IOException;
 List<DemandeResClientDto> getDemandesByClientId(Long clientId);
    List<DemandeResponseDto> getAllDemandes();

    void updateStatutDemande(Long id, StatutDemande statut);

    void affecterTechnicien(Long demandeId, Long technicienId);

     List<DemandeResponseDto> getDemandesByTechnicien(Long technicienId) ;


}
