package com.metrocal.metrocal.dto;


import lombok.Data;

@Data
public class InterventionDto {

     private String etalonUtilise;
    private Double mesureEtalon;
    private Double mesureInstrument;
    private String dureeEtalonnage;

    private Long technicienId;
    private Long demandeId;
    
}
