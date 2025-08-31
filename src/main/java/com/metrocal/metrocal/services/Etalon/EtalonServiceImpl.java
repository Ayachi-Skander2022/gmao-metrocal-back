package com.metrocal.metrocal.services.Etalon;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.metrocal.metrocal.dto.EtalonRequestDto;
import com.metrocal.metrocal.dto.EtalonResponse;
import com.metrocal.metrocal.entities.Etalon;
import com.metrocal.metrocal.entities.EtatEtalon;
import com.metrocal.metrocal.repository.EtalonRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EtalonServiceImpl implements EtalonService{
    

    private final EtalonRepository etalonRepository;
    
  
    @Override
public EtalonResponse createEtalon(String nom, String reference, String famille, String etat) {
    Etalon etalon = new Etalon();
    etalon.setNom(nom);
    etalon.setReference(reference);
    etalon.setFamille(famille);
    etalon.setEtat(EtatEtalon.valueOf(etat));

    etalon = etalonRepository.save(etalon);
    return toResponse(etalon);
}


    // ✅ DELETE
    @Override
    public void delete(Long id) {
        etalonRepository.deleteById(id);
    }

    // ✅ GET ALL
    @Override
    public List<EtalonResponse> getAll() {
        return etalonRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ✅ Mapping Entity ➜ DTO
  private EtalonResponse toResponse(Etalon etalon) {
    EtalonResponse response = new EtalonResponse();
    response.setId(etalon.getId());
    response.setCode(etalon.getCode());
    response.setNom(etalon.getNom());
    response.setReference(etalon.getReference());
    response.setFamille(etalon.getFamille());
    response.setEtat(etalon.getEtat());
    return response;
}




@Override
public EtalonResponse update(Long id, EtalonRequestDto request) {
    Etalon etalon = etalonRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Étalon introuvable avec id: " + id));

    etalon.setNom(request.getNom());
    etalon.setReference(request.getReference());
    etalon.setFamille(request.getFamille());
    etalon.setEtat(request.getEtat());

    Etalon updated = etalonRepository.save(etalon);
    return toResponse(updated);
}


    
}
