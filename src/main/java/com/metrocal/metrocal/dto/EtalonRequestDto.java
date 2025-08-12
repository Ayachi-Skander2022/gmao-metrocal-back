package com.metrocal.metrocal.dto;

import com.metrocal.metrocal.entities.EtatEtalon;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EtalonRequestDto {
    
     private Long id;
    private String nom;
    private String reference;
    private EtatEtalon etat;
}
