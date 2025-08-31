package com.metrocal.metrocal.dto;

import com.metrocal.metrocal.entities.StatutEtalonnage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstrumentRequestDto {
    
    
    private Long id;
    private String nomInstrument;
    private String referenceInstrument;
    private String constructeur;
    private String typeMesure;
    private Double minMesure;
    private Double maxMesure;
    private String uniteMesure;

    
    private StatutEtalonnage statutEtalonnage;

        private UserDto user;
}
