package com.metrocal.metrocal.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor

public class InterventionPlus  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

@ManyToOne
@JoinColumn(name = "etalon_id")
private Etalon etalonUtilise;





@OneToMany( cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Mesures> mesures = new ArrayList<>();

  private Double ecart;
    private LocalDate dateIntervention;

    private String dureeEtalonnage;

      @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne
    @JoinColumn(name = "technicien_id")
    private User technicien;

    @OneToOne
   @JoinColumn(name = "demande_id", unique = true)
    private Demande demande;



    @OneToOne
   @JoinColumn(name = "instrument_id", unique = true)
    private Instrument instrument;
   

    
}
