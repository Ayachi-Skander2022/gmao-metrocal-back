package com.metrocal.metrocal.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor

public class Etalon {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String famille;

    private String nom;

    private String reference;

    /* 
    @Lob
    @Column(columnDefinition = "TEXT")
    private String image;

    */

    @Enumerated(EnumType.STRING)
    private EtatEtalon etat;

    @PrePersist
    public void generateCode() {
        if (this.code == null || this.code.isEmpty()) {
            this.code = "ETA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    
}
