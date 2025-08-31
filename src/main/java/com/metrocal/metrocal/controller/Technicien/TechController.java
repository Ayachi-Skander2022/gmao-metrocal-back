package com.metrocal.metrocal.controller.Technicien;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.metrocal.metrocal.dto.DashboardStatsDto;
import com.metrocal.metrocal.dto.DemandeResponseDto;
import com.metrocal.metrocal.dto.EtalonResponse;
import com.metrocal.metrocal.dto.InterventionRequestDto;
import com.metrocal.metrocal.dto.InterventionResponseDto;
import com.metrocal.metrocal.services.Dashboard.DashboardService;
import com.metrocal.metrocal.services.Dashboard.DashboardSseEmitterService;
import com.metrocal.metrocal.services.Demandes.DemandeService;
import com.metrocal.metrocal.services.Etalon.EtalonService;
import com.metrocal.metrocal.services.Intervention.InterventionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tech")
@RequiredArgsConstructor



public class TechController {

           private final DemandeService demandeService;

           private final InterventionService interventionService;


         private final EtalonService etalonService;

          private final DashboardService dashboardService;
    private final DashboardSseEmitterService emitterService;

   @GetMapping("/demandes/{technicienId}")
    public ResponseEntity<List<DemandeResponseDto>> getDemandesByTechnicien(@PathVariable Long technicienId) {
        List<DemandeResponseDto> demandes = demandeService.getDemandesByTechnicien(technicienId);
        return ResponseEntity.ok(demandes);
    }


    // ✅ Créer une intervention à partir d'une demande
    @PostMapping("/createIntervention/{demandeId}")
    public ResponseEntity<InterventionResponseDto> createIntervention(
            @PathVariable Long demandeId,
            @RequestBody InterventionRequestDto dto) throws IOException {

        InterventionResponseDto response = interventionService.createIntervention(demandeId, dto);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/getInterventionBytechnicien/{id}")
    public ResponseEntity<List<InterventionResponseDto>> getByTechnicien(@PathVariable Long id) {
        return ResponseEntity.ok(interventionService.getInterventionsByTechnicien(id));
    }
    

    
    @GetMapping("/getAllEtalons")
    public ResponseEntity<List<EtalonResponse>> getAll() {
        return ResponseEntity.ok(etalonService.getAll());
    }


    
    @GetMapping("/stats")
    public DashboardStatsDto getStats() {
        return dashboardService.computeStats();
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamStats() {
        // عند الاتصال، نرسل حالة أولية مباشرةً
        SseEmitter emitter = emitterService.createEmitter();
        DashboardStatsDto stats = dashboardService.computeStats();
        try {
            emitter.send(SseEmitter.event().name("init").data(stats));
        } catch (Exception e) {
            // ignore
        }
        return emitter;
    }

    // Optional: endpoint لإجبار بث التحديث فوراً (يفيد أثناء التطوير أو من خدمات أخرى)
    @PostMapping("/refresh")
    public void refresh() throws java.io.IOException {
        DashboardStatsDto stats = dashboardService.computeStats();
        emitterService.broadcastStats(stats);
    }


 
    @GetMapping("/last-demandes")
    public List<DemandeResponseDto> getLastDemandes(
        @RequestParam(defaultValue = "5") int limit) {
        return dashboardService.getLastDemandes(limit);
    }
    
}
