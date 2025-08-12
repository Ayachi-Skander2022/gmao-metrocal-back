package com.metrocal.metrocal.dto;

import java.util.List;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class InterventionRequestDto {
    
      private String etalonUtilise;
    private List<Double> mesureEtalon;
    private List<Double> mesureInstrument;
    private String dureeEtalonnage;
}
