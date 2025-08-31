package com.metrocal.metrocal.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DashboardStatsClientDto {
     private long totalDemandes;
    private long demandesEnAttente;
    private long demandesValidees;
    private long demandesRefusees;
    private long certificatsDisponibles;
}
