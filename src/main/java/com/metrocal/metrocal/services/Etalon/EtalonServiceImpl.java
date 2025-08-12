package com.metrocal.metrocal.services.Etalon;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.metrocal.metrocal.dto.EtalonRequestDto;
import com.metrocal.metrocal.dto.EtalonResponse;
import com.metrocal.metrocal.entities.Etalon;
import com.metrocal.metrocal.entities.EtatEtalon;
import com.metrocal.metrocal.repository.EtalonRepository;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EtalonServiceImpl implements EtalonService{
    

    private final EtalonRepository etalonRepository;
    
  
    // ✅ CREATE avec image Multipart
    @SuppressWarnings("null")
    @Override
public EtalonResponse createEtalon(String nom, String reference, String etat, MultipartFile imageFile) 
    throws IOException, java.io.IOException {
    
    // Validation
    if (imageFile == null || imageFile.isEmpty()) {
        throw new IllegalArgumentException("L'image est obligatoire");
    }
    
    if (!imageFile.getContentType().startsWith("image/")) {
        throw new IllegalArgumentException("Seules les images sont acceptées");
    }
    
    // Conversion en base64
    byte[] imageBytes = imageFile.getBytes();
    String base64Image = Base64.getEncoder().encodeToString(imageBytes);

    Etalon etalon = new Etalon();
    etalon.setNom(nom);
    etalon.setReference(reference);
    etalon.setEtat(EtatEtalon.valueOf(etat));
    etalon.setImage(base64Image);

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
        response.setImage(etalon.getImage());
        response.setEtat(etalon.getEtat());
        return response;
    }



 @Override
public EtalonResponse update(Long id, EtalonRequestDto request, MultipartFile imageFile) throws java.io.IOException {
    Etalon etalon = etalonRepository.findById(id)
        .orElseThrow();

    etalon.setNom(request.getNom());
    etalon.setReference(request.getReference());


    // Assurez-vous que request.getEtat() est bien une valeur valide de l'énum
etalon.setEtat(request.getEtat());

    // Si une nouvelle image est envoyée
    if (imageFile != null && !imageFile.isEmpty()) {
        try {
            byte[] imageBytes = imageFile.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            etalon.setImage(base64Image);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture de l’image", e);
        }
    }

    Etalon updated = etalonRepository.save(etalon);
    return toResponse(updated);
}

    
}
