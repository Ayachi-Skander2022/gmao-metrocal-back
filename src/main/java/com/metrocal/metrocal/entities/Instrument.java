package com.metrocal.metrocal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor

public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "codeInstrument", unique = true)
    private String codeInstrument;

    private String nomInstrument;

    private String referenceInstrument;

    private String constructeur;

    private String typeMesure; // exemple : température, pression, etc.

    private String uniteMesure; // °C, bar, etc.

    private Double minMesure;

    private Double maxMesure;


    
    @ManyToOne(fetch = FetchType.LAZY)    
    @JoinColumn(name = "user_id")
    private User user;





    
}
