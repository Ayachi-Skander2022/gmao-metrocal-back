package com.metrocal.metrocal.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor

public class Demande {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   /*  private String nomInstrument;

    private String referenceInstrument;

    private String constructeur;

    private String typeMesure; // exemple : température, pression, etc.

    private Double minMesure;

    private Double maxMesure;

    private String uniteMesure; // °C, bar, etc.
    */

    private String typeEtalonnage; // interne, externe...

    private LocalDate dateSouhaitee; // date préférée pour la calibration

    @Column(name = "date_demande")
    private LocalDate dateDemande;

    @Enumerated(EnumType.STRING)
    private StatutEtalonnage statutEtalonnage;

    @Enumerated(EnumType.STRING)
    private StatutDemande statutDemande;


     private String statutAffectation;


      @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "technicien_id")
private User technicien;


 @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "instrument_id")
private Instrument instrument;


   @OneToOne
   @JoinColumn(name = "intervention_id", unique = true)
    private InterventionPlus intervention;

    
    @ManyToOne(fetch = FetchType.LAZY)    
    @JoinColumn(name = "user_id")
    private User user;


  



    
/* 

     public DemandeDto getDemandeDto(){
        DemandeDto demandeDto = new DemandeDto();

        demandeDto.setId(id);
        demandeDto.setNomInstrument(nomInstrument);;
        demandeDto.setReferenceInstrument(referenceInstrument);;
        demandeDto.setConstructeur(constructeur);;
        demandeDto.setTypeMesure(typeMesure);

        demandeDto.setMinMesure(minMesure);
        demandeDto.setMaxMesure(maxMesure);
        demandeDto.setDateSouhaitee(dateSouhaitee);
        demandeDto.setDateDemande(dateDemande);

        demandeDto.setUniteMesure(uniteMesure);
        demandeDto.setTypeEtalonnage(typeEtalonnage);

        demandeDto.setStatutDemande(statutDemande);      
        demandeDto.setStatutEtalonnage(statutEtalonnage); 

      
        
    // client
    if (user != null) {
        demandeDto.setUser(user.getUserDto());
    }

    // technicien
    if (technicien != null) {
        demandeDto.setTechnicien(technicien.getUserDto());
    }

     


        return demandeDto;

    }



  */

}
