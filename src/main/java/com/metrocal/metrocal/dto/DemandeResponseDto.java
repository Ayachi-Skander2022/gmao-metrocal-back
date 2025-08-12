package com.metrocal.metrocal.dto;

import java.time.LocalDate;

import com.metrocal.metrocal.entities.StatutDemande;
import com.metrocal.metrocal.entities.StatutEtalonnage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemandeResponseDto {

    private Long id;

/* 
    private String nomInstrument;
    private String referenceInstrument;
    private String constructeur;
    private String typeMesure;
    private Double minMesure;
    private Double maxMesure;
    private String uniteMesure;
    */
    private String typeEtalonnage;
    private LocalDate dateSouhaitee;
    private LocalDate dateDemande;
    private StatutDemande statutDemande;
    private StatutEtalonnage statutEtalonnage;

    private String statutAffectation; // Changement de type pour correspondre à l'entité

    private UserTechDto technicien;

private InstrumentResponseDto instrument;

    private ClientDemDto client; // ✅ user imbriqué

 


public void setInstrumentResponseDto(InstrumentResponseDto instrumentResponseDto) {
    this.instrument = instrumentResponseDto;
}

}

