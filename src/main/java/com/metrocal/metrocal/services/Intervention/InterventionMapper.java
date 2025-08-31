package com.metrocal.metrocal.services.Intervention;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.metrocal.metrocal.dto.ClientDemDto;
import com.metrocal.metrocal.dto.DemandeInfoDto;
import com.metrocal.metrocal.dto.EtalonResponse;
import com.metrocal.metrocal.dto.InstrumentResponseDto;
import com.metrocal.metrocal.dto.InterventionResponseDto;
import com.metrocal.metrocal.dto.MesureDto;
import com.metrocal.metrocal.dto.UserTechDto;
import com.metrocal.metrocal.entities.Etalon;
import com.metrocal.metrocal.entities.Instrument;
import com.metrocal.metrocal.entities.InterventionPlus;
import com.metrocal.metrocal.entities.Mesures;

@Component
public class InterventionMapper {
    
    public static EtalonResponse toEtalonDto(Etalon etalon) {
        if (etalon == null) return null;

        return new EtalonResponse(
            etalon.getId(),
            etalon.getCode(),
            etalon.getNom(),
            etalon.getReference(),
            etalon.getFamille(),
            etalon.getEtat()
        );
    }


    public InstrumentResponseDto toInstrumentDto(Instrument instrument) {
        if (instrument == null) return null;
        return new InstrumentResponseDto(
            instrument.getId(),
            instrument.getCodeInstrument(),
            instrument.getNomInstrument(),
            instrument.getReferenceInstrument(),
            instrument.getConstructeur(),
            instrument.getTypeMesure(),
            instrument.getMinMesure(),
            instrument.getMaxMesure(),
            instrument.getUniteMesure(),
            instrument.getStatutEtalonnage(),
            null
        );
    }

    public List<MesureDto> toMesureDtos(List<Mesures> mesures) {
        if (mesures == null) return new ArrayList<>();
        return mesures.stream()
            .map(m -> new MesureDto(m.getValeurEtalon(), m.getValeurInstrument(), m.getEcart()))
            .collect(Collectors.toList());
    }

    public InterventionResponseDto toResponseDto(InterventionPlus intervention) {
        return InterventionResponseDto.builder()
            .id(intervention.getId())
            .etalon(toEtalonDto(intervention.getEtalonUtilise()))   // ✅ mapping Etalon
            .mesures(toMesureDtos(intervention.getMesures()))
            .ecart(intervention.getEcart())
            .dureeEtalonnage(intervention.getDureeEtalonnage())
            .dateIntervention(intervention.getDateIntervention())
            .technicien(new UserTechDto(
                intervention.getTechnicien().getId(),
                intervention.getTechnicien().getFullName(),
                intervention.getTechnicien().getAdresse(),
                intervention.getTechnicien().getTelephone()
            ))
            .demande(new DemandeInfoDto(
                intervention.getDemande().getId(),
                intervention.getDemande().getTypeEtalonnage(),
                intervention.getDemande().getStatutEtalonnage(),
                new ClientDemDto(
                    intervention.getDemande().getUser().getId(),
                    intervention.getDemande().getUser().getFullName(),
                    intervention.getDemande().getUser().getAdresse(),
                    intervention.getDemande().getUser().getTelephone(),
                    intervention.getDemande().getUser().getEmail()
                )
            ))
            .instrument(toInstrumentDto(intervention.getInstrument()))
            .build();
    }
}
