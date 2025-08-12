package com.metrocal.metrocal.services.Etalon;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.metrocal.metrocal.dto.EtalonRequestDto;
import com.metrocal.metrocal.dto.EtalonResponse;

import io.jsonwebtoken.io.IOException;

public interface EtalonService {
    
EtalonResponse createEtalon(String nom,  String reference, String etat, MultipartFile imageFile) throws IOException, java.io.IOException;
EtalonResponse update(Long id, EtalonRequestDto request, MultipartFile imageFile) throws java.io.IOException;
    void delete(Long id);
    List<EtalonResponse> getAll() ;
}
