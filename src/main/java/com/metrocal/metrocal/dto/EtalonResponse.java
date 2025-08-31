package com.metrocal.metrocal.dto;

import com.metrocal.metrocal.entities.EtatEtalon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EtalonResponse {
   

    private Long id;
    private String code;
    private String famille;
    private String nom;
    private String reference;

    private EtatEtalon etat;
}
