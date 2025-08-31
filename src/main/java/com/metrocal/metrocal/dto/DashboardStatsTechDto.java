package com.metrocal.metrocal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsTechDto {
    private long totalInterventions;
    private long interventionsPlanifiees;
    private long interventionsRealisees;
    private long interventionsEnRetard;
}
