package com.metrocal.metrocal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class InstrumentResponseDto {
    

    private Long id;
    private String codeInstrument;
    private String nomInstrument;
    private String referenceInstrument;
    private String constructeur;
    private String typeMesure;
    private Double minMesure;
    private Double maxMesure;
    private String uniteMesure;

     private ClientDemDto client; // ✅ user imbriqué


 
}



