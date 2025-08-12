package com.metrocal.metrocal.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterventionResponseDto {

    private Long id;
    private String etalonUtilise;

    private List<MesureDto> mesures;
    private Double ecart;

    private String dureeEtalonnage;
    private LocalDate dateIntervention;

    private UserTechDto technicien;
    private DemandeInfoDto demande;

    private InstrumentResponseDto instrument;

}
