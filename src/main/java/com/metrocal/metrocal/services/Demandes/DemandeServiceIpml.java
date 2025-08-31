package com.metrocal.metrocal.services.Demandes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.metrocal.metrocal.dto.ClientDemDto;
import com.metrocal.metrocal.dto.DashboardStatsDto;
import com.metrocal.metrocal.dto.DemandeRequestDto;
import com.metrocal.metrocal.dto.DemandeResClientDto;
import com.metrocal.metrocal.dto.DemandeResponseDto;
import com.metrocal.metrocal.dto.InstrumentResponseDto;
import com.metrocal.metrocal.dto.UserTechDto;
import com.metrocal.metrocal.entities.Demande;
import com.metrocal.metrocal.entities.Instrument;
import com.metrocal.metrocal.entities.StatutDemande;
import com.metrocal.metrocal.entities.StatutEtalonnage;
import com.metrocal.metrocal.entities.User;
import com.metrocal.metrocal.entities.UserRole;
import com.metrocal.metrocal.repository.DemandeRepository;
import com.metrocal.metrocal.repository.InstrumentRepository;
import com.metrocal.metrocal.repository.UserRepository;
import com.metrocal.metrocal.services.Dashboard.DashboardService;
import com.metrocal.metrocal.services.Dashboard.DashboardSseEmitterService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class DemandeServiceIpml implements DemandeService {

    private final DemandeRepository demandeRepo;
    private final UserRepository userRepository;
    private final InstrumentRepository instrumentRepository;


     private final DashboardService dashboardService;
    private final DashboardSseEmitterService emitterService;


        // POUR CLIENT
   @Override
public DemandeResponseDto creerDemande(DemandeRequestDto demandeDto, Long instrumentId) throws IOException {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName(); // 🔥 email depuis JWT token

    Optional<User> optionalUser = userRepository.findFirstByEmail(email);
    Optional<Instrument> optionalInstrument = instrumentRepository.findById(instrumentId);

    if (optionalUser.isPresent() && optionalInstrument.isPresent()) {
        User user = optionalUser.get();
        Instrument instrument = optionalInstrument.get();

        Demande demande = new Demande();
        demande.setUser(user); // 🔗 Lien avec l'utilisateur
        demande.setInstrument(instrument); // 🔗 Lien avec instrument
        /* 
        demande.setNomInstrument(instrument.getNomInstrument());
        demande.setReferenceInstrument(instrument.getReferenceInstrument());
        demande.setConstructeur(instrument.getConstructeur());
        demande.setTypeMesure(instrument.getTypeMesure());
        demande.setUniteMesure(instrument.getUniteMesure());
        demande.setMinMesure(instrument.getMinMesure());
        demande.setMaxMesure(instrument.getMaxMesure());*/
        demande.setTypeEtalonnage(demandeDto.getTypeEtalonnage());
        demande.setDateSouhaitee(demandeDto.getDateSouhaitee());
        demande.setDateDemande(LocalDate.now());
        demande.setStatutDemande(StatutDemande.PENDING);
        demande.setStatutEtalonnage(StatutEtalonnage.EN_ATTENTE);
        demande.getInstrument().setStatutEtalonnage(StatutEtalonnage.EN_ATTENTE);
        Demande savedDemande = demandeRepo.save(demande);

        DashboardStatsDto stats = dashboardService.computeStats();
        emitterService.broadcastStats(stats);

        // ✨ Mapper vers DTO
        DemandeResponseDto responseDto = new DemandeResponseDto();
        responseDto.setId(savedDemande.getId());
        /* 
        responseDto.setNomInstrument(savedDemande.getNomInstrument());
        responseDto.setReferenceInstrument(savedDemande.getReferenceInstrument());
        responseDto.setConstructeur(savedDemande.getConstructeur());
        responseDto.setTypeMesure(savedDemande.getTypeMesure());
        responseDto.setUniteMesure(savedDemande.getUniteMesure());
        responseDto.setMinMesure(savedDemande.getMinMesure());
        responseDto.setMaxMesure(savedDemande.getMaxMesure());*/

        responseDto.setTypeEtalonnage(savedDemande.getTypeEtalonnage());

        responseDto.setDateSouhaitee(savedDemande.getDateSouhaitee());
        responseDto.setDateDemande(savedDemande.getDateDemande());
        responseDto.setStatutDemande(savedDemande.getStatutDemande());
        responseDto.setStatutEtalonnage(savedDemande.getStatutEtalonnage());

        // 📦 Client dans la réponse
        ClientDemDto clientDto = new ClientDemDto(
                user.getId(),
                user.getFullName(),
                user.getAdresse(),
                user.getTelephone(),
                user.getEmail()
        );
        responseDto.setClient(clientDto);

        // 📦 Instrument dans la réponse
        InstrumentResponseDto instrumentResponseDto = new InstrumentResponseDto(
                instrument.getId(),
                instrument.getCodeInstrument(),
                instrument.getNomInstrument(),
                instrument.getReferenceInstrument(),
                instrument.getConstructeur(),
                instrument.getTypeMesure(),
                instrument.getMinMesure(),
                instrument.getMaxMesure(),
                instrument.getUniteMesure(),
                instrument.getStatutEtalonnage(),
                null // ou instrument.getClient() si tu veux l’imbriquer
        );
        responseDto.setInstrument(instrumentResponseDto);

        return responseDto;
    }

    return null;
}



@Override
public List<DemandeResponseDto> getAllDemandes() {
    List<Demande> demandes = demandeRepo.findAll();

    return demandes.stream().map(demande -> {
        DemandeResponseDto dto = new DemandeResponseDto();

        dto.setId(demande.getId());
        /* 
        dto.setNomInstrument(demande.getNomInstrument());
        dto.setReferenceInstrument(demande.getReferenceInstrument());
        dto.setConstructeur(demande.getConstructeur());
        dto.setTypeMesure(demande.getTypeMesure());
        dto.setUniteMesure(demande.getUniteMesure());
        dto.setMinMesure(demande.getMinMesure());
        dto.setMaxMesure(demande.getMaxMesure());
        */
         dto.setTypeEtalonnage(demande.getTypeEtalonnage());

        dto.setDateDemande(demande.getDateDemande());
        dto.setDateSouhaitee(demande.getDateSouhaitee());
        dto.setStatutDemande(demande.getStatutDemande());
        dto.setStatutEtalonnage(demande.getStatutEtalonnage());
        dto.setStatutAffectation(demande.getStatutAffectation());

        // ✅ Client (User)
        User user = demande.getUser();
        if (user != null) {
            dto.setClient(new ClientDemDto(
                user.getId(),
                user.getFullName(),
                user.getAdresse(),
                user.getTelephone(),
                user.getEmail()
            ));
        }

        // ✅ Technicien
        User technicien = demande.getTechnicien();
        if (technicien != null) {
            dto.setTechnicien(new UserTechDto(
                technicien.getId(),
                technicien.getFullName(),
                technicien.getAdresse(),
                technicien.getTelephone()
            ));
        }

        // ✅ Instrument
        Instrument instrument = demande.getInstrument();
        if (instrument != null) {
            dto.setInstrumentResponseDto(new InstrumentResponseDto(
                instrument.getId(),
                instrument.getCodeInstrument(),
                instrument.getNomInstrument(),
                instrument.getReferenceInstrument(),
                instrument.getConstructeur(),
                instrument.getTypeMesure(),
                instrument.getMinMesure(),
                instrument.getMaxMesure(),
                instrument.getUniteMesure(),
                instrument.getStatutEtalonnage(),
                null // ou `instrument.getClient()` si le modèle supporte
            ));
        }

        return dto;
    }).collect(Collectors.toList());
}





     // POUR CLIENT
@Override
public List<DemandeResClientDto> getDemandesByClientId(Long clientId) {
    List<Demande> demandes = demandeRepo.findByUser_id(clientId);

    return demandes.stream().map(demande -> {
        DemandeResClientDto dto = new DemandeResClientDto();
        dto.setId(demande.getId());

      /* 
       dto.setNomInstrument(demande.getNomInstrument());
        dto.setReferenceInstrument(demande.getReferenceInstrument());
        dto.setConstructeur(demande.getConstructeur());
        dto.setTypeMesure(demande.getTypeMesure());
        dto.setUniteMesure(demande.getUniteMesure());
        dto.setMinMesure(demande.getMinMesure());
        dto.setMaxMesure(demande.getMaxMesure());*/

                dto.setTypeEtalonnage(demande.getTypeEtalonnage());

        dto.setDateDemande(demande.getDateDemande());
        dto.setDateSouhaitee(demande.getDateSouhaitee());
        dto.setStatutDemande(demande.getStatutDemande());
        dto.setStatutEtalonnage(demande.getStatutEtalonnage());

     
        // ✅ Instrument
        Instrument instrument = demande.getInstrument();
        if (instrument != null) {
            dto.setInstrument(new InstrumentResponseDto(
                instrument.getId(),
                instrument.getCodeInstrument(),
                instrument.getNomInstrument(),
                instrument.getReferenceInstrument(),
                instrument.getConstructeur(),
                instrument.getTypeMesure(),
                instrument.getMinMesure(),
                instrument.getMaxMesure(),
                instrument.getUniteMesure(),
                instrument.getStatutEtalonnage(),
                null // ou instrument.getClient() si tu veux
            ));

               // ✅ User (client)
               /* 
        User user = demande.getUser();
        if (user != null) {
            dto.setClient(new ClientDemDto(
                user.getId(),
                user.getFullName(),
                user.getAdresse(),
                user.getTelephone()
            ));  
        }

*/

        }

        return dto;
    }).collect(Collectors.toList());
}



@Override
  public void updateStatutDemande(Long id, StatutDemande statut) {
        Demande demande = demandeRepo.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Demande non trouvée"));

        demande.setStatutDemande(statut);
        demandeRepo.save(demande);
    }


@Override
@Transactional
public void affecterTechnicien(Long demandeId, Long technicienId) {
    Demande demande = demandeRepo.findById(demandeId)
        .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

    User technicien = userRepository.findById(technicienId)
        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

    if (!UserRole.TECHNICIEN.equals(technicien.getUserRole())) {
        throw new RuntimeException("L'utilisateur sélectionné n'est pas un technicien");
    }

    demande.setTechnicien(technicien);
    demande.setStatutAffectation("Affecter");
    demandeRepo.save(demande);
}





 
@Override
public List<DemandeResponseDto> getDemandesByTechnicien(Long technicienId) {
    List<Demande> demandes = demandeRepo.findByTechnicien_Id(technicienId);

    return demandes.stream().map(demande -> {
        DemandeResponseDto dto = new DemandeResponseDto();
        dto.setId(demande.getId());
        /* 
        dto.setNomInstrument(demande.getNomInstrument());
        dto.setReferenceInstrument(demande.getReferenceInstrument());
        dto.setConstructeur(demande.getConstructeur());
        dto.setTypeMesure(demande.getTypeMesure());
        dto.setUniteMesure(demande.getUniteMesure());
        dto.setMinMesure(demande.getMinMesure());
        dto.setMaxMesure(demande.getMaxMesure());*/
       dto.setTypeEtalonnage(demande.getTypeEtalonnage());

        dto.setDateDemande(demande.getDateDemande());
        dto.setDateSouhaitee(demande.getDateSouhaitee());
        dto.setStatutDemande(demande.getStatutDemande());
        dto.setStatutEtalonnage(demande.getStatutEtalonnage());
        dto.setStatutAffectation(demande.getStatutAffectation());


        // ✅ Instrument
        Instrument instrument = demande.getInstrument();
        if (instrument != null) {
            dto.setInstrumentResponseDto(new InstrumentResponseDto(
                instrument.getId(),
                instrument.getCodeInstrument(),
                instrument.getNomInstrument(),
                instrument.getReferenceInstrument(),
                instrument.getConstructeur(),
                instrument.getTypeMesure(),
                instrument.getMinMesure(),
                instrument.getMaxMesure(),
                instrument.getUniteMesure(),
                instrument.getStatutEtalonnage(),
                null // ou instrument.getClient() si nécessaire
            ));


            
        // ✅ Client
        User user = demande.getUser();
        if (user != null) {
            dto.setClient(new ClientDemDto(
                user.getId(),
                user.getFullName(),
                user.getAdresse(),
                user.getTelephone(),
                user.getEmail()
            ));
        }

        // ✅ Technicien
        User technicien = demande.getTechnicien();
        if (technicien != null) {
            dto.setTechnicien(new UserTechDto(
                technicien.getId(),
                technicien.getFullName(),
                technicien.getAdresse(),
                technicien.getTelephone()
            ));
        }
        
        }

        return dto;
    }).collect(Collectors.toList());
}





     





}
