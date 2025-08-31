package com.metrocal.metrocal.services.Demandes;

import org.springframework.stereotype.Component;

import com.metrocal.metrocal.dto.ClientDemDto;
import com.metrocal.metrocal.dto.DemandeResponseDto;
import com.metrocal.metrocal.dto.InstrumentResponseDto;
import com.metrocal.metrocal.entities.Demande;
import com.metrocal.metrocal.entities.Instrument;
import com.metrocal.metrocal.entities.User;

@Component
public class DemandeMapper {
    

        public DemandeResponseDto mapToDemandeResponseDto(Demande demande) {
        DemandeResponseDto dto = new DemandeResponseDto();
        dto.setId(demande.getId());
        dto.setTypeEtalonnage(demande.getTypeEtalonnage());
        dto.setDateDemande(demande.getDateDemande());
        dto.setDateSouhaitee(demande.getDateSouhaitee());
        dto.setStatutDemande(demande.getStatutDemande());
        dto.setStatutEtalonnage(demande.getStatutEtalonnage());
        dto.setStatutAffectation(demande.getStatutAffectation());

        // Client
        User user = demande.getUser();
        if (user != null) {
            dto.setClient(new ClientDemDto(
                user.getId(),
                user.getFullName(),
                user.getAdresse(),
                user.getTelephone(),
                user.getEmail()
            ));
        }

       

        // Instrument
        Instrument instrument = demande.getInstrument();
        if (instrument != null) {
            dto.setInstrumentResponseDto(new InstrumentResponseDto(
                instrument.getId(),
                instrument.getCodeInstrument(),
                instrument.getNomInstrument(),
                instrument.getReferenceInstrument(),
                instrument.getConstructeur(),
                instrument.getTypeMesure(),
                instrument.getMinMesure(),
                instrument.getMaxMesure(),
                instrument.getUniteMesure(),
                instrument.getStatutEtalonnage(), null
            ));
        }
        return dto;
    }


}
