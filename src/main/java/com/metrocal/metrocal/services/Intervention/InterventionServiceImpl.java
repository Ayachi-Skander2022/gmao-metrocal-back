package com.metrocal.metrocal.services.Intervention;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.metrocal.metrocal.dto.ClientDemDto;
import com.metrocal.metrocal.dto.DashboardStatsDto;
import com.metrocal.metrocal.dto.DemandeInfoDto;
import com.metrocal.metrocal.dto.InstrumentResponseDto;
import com.metrocal.metrocal.dto.InterventionRequestDto;
import com.metrocal.metrocal.dto.InterventionResponseDto;
import com.metrocal.metrocal.dto.MesureDto;
import com.metrocal.metrocal.dto.UserTechDto;
import com.metrocal.metrocal.entities.Demande;
import com.metrocal.metrocal.entities.Instrument;
import com.metrocal.metrocal.entities.InterventionPlus;
import com.metrocal.metrocal.entities.Mesures;
import com.metrocal.metrocal.entities.StatutEtalonnage;
import com.metrocal.metrocal.entities.User;
import com.metrocal.metrocal.repository.DemandeRepository;
import com.metrocal.metrocal.repository.InterventionRepository;
import com.metrocal.metrocal.repository.UserRepository;
import com.metrocal.metrocal.services.Dashboard.DashboardService;
import com.metrocal.metrocal.services.Dashboard.DashboardSseEmitterService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterventionServiceImpl implements InterventionService{


    private final InterventionRepository interventionRepository;


    private final UserRepository userRepository;

    
    private final DemandeRepository demandeRepository;

    private final DashboardService dashboardService;
    private final DashboardSseEmitterService emitterService;



@Override
public InterventionResponseDto createIntervention(Long demandeId, InterventionRequestDto dto) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    User technicien = userRepository.findFirstByEmail(email)
        .orElseThrow(() -> new RuntimeException("Technicien non trouvé"));

    Demande demande = demandeRepository.findById(demandeId)
        .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

    if (interventionRepository.existsByDemande_Id(demandeId)) {
        throw new IllegalStateException("Une intervention existe déjà pour cette demande.");
    }

    InterventionPlus intervention = new InterventionPlus();
    intervention.setEtalonUtilise(dto.getEtalonUtilise());
    intervention.setDureeEtalonnage(dto.getDureeEtalonnage());
    intervention.setDateIntervention(LocalDate.now());
    intervention.setTechnicien(technicien);
    intervention.setDemande(demande);

    List<Mesures> mesuresEntities = new ArrayList<>();
    double totalEcart = 0;

    if (dto.getMesureEtalon() != null && dto.getMesureInstrument() != null &&
        dto.getMesureEtalon().size() == dto.getMesureInstrument().size()) {

        for (int i = 0; i < dto.getMesureEtalon().size(); i++) {
            Mesures mesure = new Mesures();
            mesure.setValeurEtalon(dto.getMesureEtalon().get(i));
            mesure.setValeurInstrument(dto.getMesureInstrument().get(i));
            mesure.setIntervention(intervention);

            mesure.calculerEcart();

            totalEcart += mesure.getEcart();
            mesuresEntities.add(mesure);
        }

        double ecartMoyen = totalEcart / dto.getMesureEtalon().size();
        intervention.setEcart(ecartMoyen);
    }

    intervention.setMesures(mesuresEntities);
    demande.setStatutEtalonnage(StatutEtalonnage.ETALONNE);

    InterventionPlus saved = interventionRepository.save(intervention);

        DashboardStatsDto stats = dashboardService.computeStats();
        emitterService.broadcastStats(stats);

    List<MesureDto> mesuresDtos = mesuresEntities.stream()
        .map(m -> new MesureDto(m.getValeurEtalon(), m.getValeurInstrument(), m.getEcart()))
        .collect(Collectors.toList());

    // 🔹 Intégration de InstrumentResponseDto
    Instrument instrument = demande.getInstrument();
    InstrumentResponseDto instrumentDto = null;
    if (instrument != null) {
        instrumentDto = new InstrumentResponseDto(
            instrument.getId(),
            instrument.getCodeInstrument(),
            instrument.getNomInstrument(),
            instrument.getReferenceInstrument(),
            instrument.getConstructeur(),
            instrument.getTypeMesure(),
            instrument.getMinMesure(),
            instrument.getMaxMesure(),
            instrument.getUniteMesure(),
            null // ou instrument.getClient() si nécessaire
        );
    }

    return InterventionResponseDto.builder()
        .id(saved.getId())
        .etalonUtilise(saved.getEtalonUtilise())
        .mesures(mesuresDtos)
        .ecart(saved.getEcart())
        .dureeEtalonnage(saved.getDureeEtalonnage())
        .dateIntervention(saved.getDateIntervention())

        .technicien(new UserTechDto(
            technicien.getId(),
            technicien.getFullName(),
            technicien.getAdresse(),
            technicien.getTelephone()
        ))

        .demande(new DemandeInfoDto(
            demande.getId(),
            /* 
            demande.getNomInstrument(),
            demande.getReferenceInstrument(),
            demande.getConstructeur(),
            demande.getTypeMesure(),
            demande.getUniteMesure(),*/

             demande.getTypeEtalonnage(),
            demande.getStatutEtalonnage(),
            new ClientDemDto(
                demande.getUser().getId(),
                demande.getUser().getFullName(),
                demande.getUser().getAdresse(),
                demande.getUser().getTelephone(),
                demande.getUser().getEmail()
            )
        ))

        .instrument(instrumentDto) // ✅ ajout ici
        .build();
}





@Override
public List<InterventionResponseDto> getInterventionsByTechnicien(Long techId) {
    List<InterventionPlus> interventions = interventionRepository.findByTechnicien_id(techId);

    System.out.println("Interventions trouvées pour techId=" + techId + " : " + interventions.size());

    return interventions.stream().map(intervention -> {

        // Map mesures
        List<MesureDto> mesureDtos = intervention.getMesures().stream().map(m -> new MesureDto(
            m.getValeurInstrument(),
            m.getValeurEtalon(),
            m.getEcart()
        )).collect(Collectors.toList());

        // Infos technicien & demande
        Demande demande = intervention.getDemande();
        User client = demande.getUser();
        User technicien = intervention.getTechnicien();

        // 🔹 Instrument
        Instrument instrument = demande.getInstrument();
        InstrumentResponseDto instrumentDto = null;
        if (instrument != null) {
            instrumentDto = new InstrumentResponseDto(
                instrument.getId(),
                instrument.getCodeInstrument(),
                instrument.getNomInstrument(),
                instrument.getReferenceInstrument(),
                instrument.getConstructeur(),
                instrument.getTypeMesure(),
                instrument.getMinMesure(),
                instrument.getMaxMesure(),
                instrument.getUniteMesure(),
                null // ou instrument.getClient() si applicable
            );
        }

        return InterventionResponseDto.builder()
            .id(intervention.getId())
            .etalonUtilise(intervention.getEtalonUtilise())
            .dateIntervention(intervention.getDateIntervention())
            .dureeEtalonnage(intervention.getDureeEtalonnage())
            .mesures(mesureDtos)
            .ecart(intervention.getEcart())

            .technicien(new UserTechDto(
                technicien.getId(),
                technicien.getFullName(),
                technicien.getAdresse(),
                technicien.getTelephone()
            ))

            .demande(new DemandeInfoDto(
                demande.getId(),

                /* 
                demande.getNomInstrument(),
                demande.getReferenceInstrument(),
                demande.getConstructeur(),
                demande.getTypeMesure(),
                demande.getUniteMesure(),*/
            
                demande.getTypeEtalonnage(),

                demande.getStatutEtalonnage(),
                new ClientDemDto(
                    client.getId(),
                    client.getFullName(),
                    client.getAdresse(),
                    client.getTelephone(),
                    client.getEmail()
                )
            ))

            .instrument(instrumentDto) // ✅ Ajouté ici
            .build();

    }).collect(Collectors.toList());
} 



@Override
public List<InterventionResponseDto> getInterventionsByClient(Long clientId) {
    List<InterventionPlus> interventions = interventionRepository.findByDemande_User_Id(clientId);

    System.out.println("Interventions trouvées pour clientId=" + clientId + " : " + interventions.size());

    return interventions.stream().map(intervention -> {

        // Map mesures
        List<MesureDto> mesureDtos = intervention.getMesures().stream().map(m -> new MesureDto(
            m.getValeurInstrument(),
            m.getValeurEtalon(),
            m.getEcart()
        )).collect(Collectors.toList());

        // Infos technicien & demande
        Demande demande = intervention.getDemande();
        User client = demande.getUser();
        User technicien = intervention.getTechnicien();

        // Instrument
        Instrument instrument = demande.getInstrument();
        InstrumentResponseDto instrumentDto = null;
        if (instrument != null) {
            instrumentDto = new InstrumentResponseDto(
                instrument.getId(),
                instrument.getCodeInstrument(),
                instrument.getNomInstrument(),
                instrument.getReferenceInstrument(),
                instrument.getConstructeur(),
                instrument.getTypeMesure(),
                instrument.getMinMesure(),
                instrument.getMaxMesure(),
                instrument.getUniteMesure(),
                null
            );
        }

        return InterventionResponseDto.builder()
            .id(intervention.getId())
            .etalonUtilise(intervention.getEtalonUtilise())
            .dateIntervention(intervention.getDateIntervention())
            .dureeEtalonnage(intervention.getDureeEtalonnage())
            .mesures(mesureDtos)
            .ecart(intervention.getEcart())

            .technicien(new UserTechDto(
                technicien.getId(),
                technicien.getFullName(),
                technicien.getAdresse(),
                technicien.getTelephone()
            ))

            .demande(new DemandeInfoDto(
                demande.getId(),
                demande.getTypeEtalonnage(),
                demande.getStatutEtalonnage(),
                new ClientDemDto(
                    client.getId(),
                    client.getFullName(),
                    client.getAdresse(),
                    client.getTelephone(),
                    client.getEmail()
                )
            ))

            .instrument(instrumentDto)
            .build();

    }).collect(Collectors.toList());
}



@Override
public List<InterventionResponseDto> getAllInterventions() {

    List<InterventionPlus> interventions = interventionRepository.findAll();

    return interventions.stream().map(intervention -> {

        // Map mesures
        List<MesureDto> mesureDtos = intervention.getMesures().stream().map(m -> new MesureDto(
            m.getValeurInstrument(),
            m.getValeurEtalon(),
            m.getEcart()
        )).collect(Collectors.toList());

        // Infos technicien & demande
        Demande demande = intervention.getDemande();
        User client = demande.getUser();
        User technicien = intervention.getTechnicien();

        // 🔹 Instrument
        Instrument instrument = demande.getInstrument();
        InstrumentResponseDto instrumentDto = null;
        if (instrument != null) {
            instrumentDto = new InstrumentResponseDto(
                instrument.getId(),
                instrument.getCodeInstrument(),
                instrument.getNomInstrument(),
                instrument.getReferenceInstrument(),
                instrument.getConstructeur(),
                instrument.getTypeMesure(),
                instrument.getMinMesure(),
                instrument.getMaxMesure(),
                instrument.getUniteMesure(),
                null // ou instrument.getClient() si applicable
            );
        }

        return InterventionResponseDto.builder()
            .id(intervention.getId())
            .etalonUtilise(intervention.getEtalonUtilise())
            .dateIntervention(intervention.getDateIntervention())
            .dureeEtalonnage(intervention.getDureeEtalonnage())
            .mesures(mesureDtos)
            .ecart(intervention.getEcart())

            .technicien(new UserTechDto(
                technicien.getId(),
                technicien.getFullName(),
                technicien.getAdresse(),
                technicien.getTelephone()
            ))

              .demande(new DemandeInfoDto(
                demande.getId(),

                /* 
                demande.getNomInstrument(),
                demande.getReferenceInstrument(),
                demande.getConstructeur(),
                demande.getTypeMesure(),
                demande.getUniteMesure(),*/
            
                demande.getTypeEtalonnage(),

                demande.getStatutEtalonnage(),
                new ClientDemDto(
                    client.getId(),
                    client.getFullName(),
                    client.getAdresse(),
                    client.getTelephone(),
                    client.getEmail()
                )
            ))

            .instrument(instrumentDto) // ✅ Ajout ici
            .build();

    }).collect(Collectors.toList());
}


    
}
