package com.metrocal.metrocal.services.Intervention;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.metrocal.metrocal.dto.DashboardStatsDto;
import com.metrocal.metrocal.dto.InterventionRequestDto;
import com.metrocal.metrocal.dto.InterventionResponseDto;
import com.metrocal.metrocal.dto.UserTechDto;
import com.metrocal.metrocal.entities.Demande;
import com.metrocal.metrocal.entities.Etalon;
import com.metrocal.metrocal.entities.Instrument;
import com.metrocal.metrocal.entities.InterventionPlus;
import com.metrocal.metrocal.entities.Mesures;
import com.metrocal.metrocal.entities.StatutEtalonnage;
import com.metrocal.metrocal.entities.User;
import com.metrocal.metrocal.repository.DemandeRepository;
import com.metrocal.metrocal.repository.EtalonRepository;
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

      private final InterventionMapper interventionMapper;

         private final EtalonRepository etalonRepository;



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

        Etalon etalon = etalonRepository.findById(dto.getEtalonId())
            .orElseThrow(() -> new RuntimeException("Étalon non trouvé"));

        InterventionPlus intervention = new InterventionPlus();
        intervention.setEtalonUtilise(etalon);
        intervention.setDureeEtalonnage(dto.getDureeEtalonnage());
        intervention.setDateIntervention(LocalDate.now());
        intervention.setTechnicien(technicien);
        intervention.setDemande(demande);
        intervention.setInstrument(demande.getInstrument());

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
        demande.getInstrument().setStatutEtalonnage(StatutEtalonnage.ETALONNE);

        InterventionPlus saved = interventionRepository.save(intervention);

        DashboardStatsDto stats = dashboardService.computeStats();
        emitterService.broadcastStats(stats);

        return interventionMapper.toResponseDto(saved);
    }



 @Override
    public List<InterventionResponseDto> getInterventionsByTechnicien(Long techId) {
        return interventionRepository.findByTechnicien_id(techId)
                .stream()
                .map(interventionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InterventionResponseDto> getInterventionsByClient(Long clientId) {
        return interventionRepository.findByDemande_User_Id(clientId)
                .stream()
                .map(interventionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InterventionResponseDto> getAllInterventions() {
        return interventionRepository.findAll()
                .stream()
                .map(interventionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    
}
