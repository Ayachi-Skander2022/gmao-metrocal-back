package com.metrocal.metrocal.controller.Company;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

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
import com.metrocal.metrocal.dto.DemandeRequestDto;
import com.metrocal.metrocal.dto.DemandeResClientDto;
import com.metrocal.metrocal.dto.DemandeResponseDto;
import com.metrocal.metrocal.dto.InstrumentRequestDto;
import com.metrocal.metrocal.dto.InstrumentResponseDto;
import com.metrocal.metrocal.dto.InterventionResponseDto;
import com.metrocal.metrocal.services.Dashboard.DashboardService;
import com.metrocal.metrocal.services.Dashboard.DashboardSseEmitterService;
import com.metrocal.metrocal.services.Demandes.DemandeService;
import com.metrocal.metrocal.services.Instrument.InstrumentService;
import com.metrocal.metrocal.services.Intervention.InterventionService;
import com.metrocal.metrocal.services.PdfCertificate.PdfCertificateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/client")
@RequiredArgsConstructor



public class ClientController {
    
       private final DemandeService demandeService;

        private final InstrumentService instrumentService;

        private final InterventionService interventionService;

        private final PdfCertificateService pdfCertificateService;

         private final DashboardService dashboardService;
    private final DashboardSseEmitterService emitterService;



          @PostMapping("/demandes/{instrumentId}")
        public ResponseEntity<?> creerDemande(@RequestBody DemandeRequestDto demandeDto, @PathVariable Long instrumentId, Authentication authentication) throws IOException {

         DemandeResponseDto responseDto = demandeService.creerDemande(demandeDto, instrumentId);

         if (responseDto != null) {
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
       }
    }


      @GetMapping("/getByClient/{clientId}")
    public ResponseEntity<List<DemandeResClientDto>> getDemandesByClientId(@PathVariable Long clientId) {
        List<DemandeResClientDto> demandes = demandeService.getDemandesByClientId(clientId);
        return ResponseEntity.ok(demandes);
    }



    
          @PostMapping("/addInstrument")
        public ResponseEntity<?> creerInstrument(@RequestBody InstrumentRequestDto instrumentDto, Authentication authentication) {

         InstrumentResponseDto responseDto = instrumentService.creerInstrument(instrumentDto);

         if (responseDto != null) {
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");

       }
    }




       @GetMapping("/getInstrumentsByClient/{clientId}")
    public ResponseEntity<List<InstrumentResponseDto>> getInstrumentsByClientId(@PathVariable Long clientId) {
        List<InstrumentResponseDto> instruments = instrumentService.getInstrumentByClientId(clientId);
        return ResponseEntity.ok(instruments);
    }


    @GetMapping("/getInterventionByClient/{clientId}")
public List<InterventionResponseDto> getInterventionsByClient(@PathVariable Long clientId) {
    return interventionService.getInterventionsByClient(clientId);
}




  @GetMapping("intervention/{id}/certificate")
public ResponseEntity<byte[]> downloadCertificate(@PathVariable Long id) throws Exception {
    InterventionResponseDto interventionDto = interventionService.getAllInterventions().stream()
            .filter(i -> i.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Intervention non trouvée"));

    byte[] pdf = pdfCertificateService.generateCalibrationCertificate(interventionDto);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("attachment", "certificat_" + id + ".pdf");

    return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
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
