package com.metrocal.metrocal.services.Dashboard;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.metrocal.metrocal.dto.DashboardStatsClientDto;
import com.metrocal.metrocal.dto.DashboardStatsDto;
import com.metrocal.metrocal.dto.DashboardStatsTechDto;
import com.metrocal.metrocal.dto.DemandeResponseDto;
import com.metrocal.metrocal.entities.Demande;
import com.metrocal.metrocal.repository.DemandeRepository;
import com.metrocal.metrocal.repository.InterventionRepository;
import com.metrocal.metrocal.repository.UserRepository;
import com.metrocal.metrocal.services.Demandes.DemandeMapper;

@Service
public class DashboardService {


     private final DemandeRepository demandeRepository;
    private final InterventionRepository interventionRepository;
    private final UserRepository userRepository;

  private final DemandeMapper demandeMapper;

    public DashboardService(DemandeRepository demandeRepository, DemandeMapper demandeMapper, UserRepository userRepository, InterventionRepository interventionRepository) {
        this.demandeRepository = demandeRepository;
        this.interventionRepository = interventionRepository;
        this.userRepository = userRepository;
        this.demandeMapper = demandeMapper;
    }

    public DashboardStatsDto computeStats() {
        long totalDemandes = demandeRepository.countAll();
        long DemandeEnAttente = demandeRepository.countPending();
        long  DemandeAcceptees = demandeRepository.countApproved();
        long totalInterventions = interventionRepository.countAll();
        long totalClients = userRepository.countClients();
        return new DashboardStatsDto(totalDemandes, DemandeEnAttente, DemandeAcceptees, totalInterventions, totalClients);
    }

  public List<DemandeResponseDto> getLastDemandes(int limit) {
        List<Demande> lastDemandes = demandeRepository.findLastDemandes(PageRequest.of(0, limit));
        
        return lastDemandes.stream()
                .map(d -> demandeMapper.mapToDemandeResponseDto(d)) // ✅ Appel via l’instance injectée
                .collect(Collectors.toList());
    }


/*
 * 
 *  // 🔹 Stats pour un Client
    public DashboardStatsClientDto computeClientStats(Long clientId) {
        long totalDemandes = demandeRepository.countWithCertificatByClientId(clientId);
    
        long certificatsDisponibles = demandeRepository.countWithCertificatByClientId(clientId);

        return new DashboardStatsClientDto(
            
        );
    }

    // 🔹 Stats pour un Technicien
    public DashboardStatsTechDto computeTechnicienStats(Long technicienId) {
      

        return new DashboardStatsTechDto(
             
        );
    }
 * 
 */
   
}
