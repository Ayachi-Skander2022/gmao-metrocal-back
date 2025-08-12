package com.metrocal.metrocal.dto;

import lombok.Data;

@Data
public class MesureDto {
  private  Double valeurInstrument;
    private Double valeurEtalon;
    private Double ecart;

    public MesureDto(Double valeurEtalon, Double valeurInstrument, Double ecart) {
        this.valeurEtalon = valeurEtalon;
        this.valeurInstrument = valeurInstrument;
        this.ecart = ecart;
    }

}

