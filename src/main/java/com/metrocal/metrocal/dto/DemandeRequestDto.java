package com.metrocal.metrocal.dto;

import java.time.LocalDate;

import com.metrocal.metrocal.entities.StatutDemande;
import com.metrocal.metrocal.entities.StatutEtalonnage;

import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor

public class DemandeRequestDto {
    

 
    private Long id;

    /* 

    private String nomInstrument;

    private String referenceInstrument;

    private String constructeur;

    private String typeMesure; // exemple : température, pression, etc.

    private Double minMesure;

    private Double maxMesure;

    private String uniteMesure; // °C, bar, etc.
    */

    private String typeEtalonnage; // interne, externe...

    private LocalDate dateSouhaitee; // date préférée pour la calibration

    private LocalDate dateDemande;

    private StatutDemande statutDemande;

    private StatutEtalonnage statutEtalonnage;

  private UserDto technicien;
     private UserDto user;
     private InstrumentRequestDto instrumentRequestDto;

     
  


   

}
