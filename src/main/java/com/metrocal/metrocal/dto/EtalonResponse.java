package com.metrocal.metrocal.dto;

import com.metrocal.metrocal.entities.EtatEtalon;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EtalonResponse {
    private Long id;
    private String code;
    private String nom;
    private String reference;
    private String image;
    private EtatEtalon etat;
}
