package com.metrocal.metrocal.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DashboardStatsDto {
    
    private long totalDemandes;
    private long demandesEnAttente;
    private long DemandeAcceptees;
   private long totalInterventions;
      private long totalClients;

   public DashboardStatsDto(long totalDemandes, long demandesEnAttente, long DemandeAcceptees,
        long totalInterventions, long totalClients) {
    this.totalDemandes = totalDemandes;
    this.demandesEnAttente = demandesEnAttente;
    this.DemandeAcceptees = DemandeAcceptees;
    this.totalInterventions = totalInterventions;
    this.totalClients = totalClients;
   }

}
