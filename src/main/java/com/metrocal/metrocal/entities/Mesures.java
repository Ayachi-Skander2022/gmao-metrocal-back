package com.metrocal.metrocal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Mesures {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double valeurInstrument;
    private Double valeurEtalon;
    private Double ecart; // calculé et enregistré si nécessaire

    @ManyToOne
@JoinColumn(name = "intervention_id")
private InterventionPlus intervention;
    // Calculer automatiquement l'écart si besoin
    public void calculerEcart() {
        if (valeurInstrument != null && valeurEtalon != null) {
            this.ecart = valeurInstrument - valeurEtalon;
        }
    }

}
