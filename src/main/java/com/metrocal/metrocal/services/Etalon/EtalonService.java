package com.metrocal.metrocal.services.Etalon;

import java.util.List;


import com.metrocal.metrocal.dto.EtalonRequestDto;
import com.metrocal.metrocal.dto.EtalonResponse;


public interface EtalonService {
    
 EtalonResponse createEtalon(String nom, String reference, String famille, String etat);
EtalonResponse update(Long id, EtalonRequestDto request);
    void delete(Long id);
    List<EtalonResponse> getAll() ;
}
