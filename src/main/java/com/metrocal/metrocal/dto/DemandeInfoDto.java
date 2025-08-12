package com.metrocal.metrocal.dto;

import com.metrocal.metrocal.entities.StatutEtalonnage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DemandeInfoDto {
    
    private Long id;
    /* 
    private String nomInstrument;
    private String referenceInstrument;
    private String constructeur;
    private String typeMesure; */
    private String typeEtalonnage;

    private StatutEtalonnage statutEtalonnage;

      private ClientDemDto client;

}
