package com.metrocal.metrocal.controller.Admin;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.metrocal.metrocal.dto.DashboardStatsDto;
import com.metrocal.metrocal.dto.DemandeResponseDto;
import com.metrocal.metrocal.dto.EtalonRequestDto;
import com.metrocal.metrocal.dto.EtalonResponse;
import com.metrocal.metrocal.dto.InstrumentResponseDto;
import com.metrocal.metrocal.dto.InterventionResponseDto;
import com.metrocal.metrocal.dto.UserDto;
import com.metrocal.metrocal.entities.EtatEtalon;
import com.metrocal.metrocal.entities.StatutDemande;
import com.metrocal.metrocal.services.Dashboard.DashboardService;
import com.metrocal.metrocal.services.Dashboard.DashboardSseEmitterService;
import com.metrocal.metrocal.services.Demandes.DemandeService;
import com.metrocal.metrocal.services.Etalon.EtalonService;
import com.metrocal.metrocal.services.Instrument.InstrumentService;
import com.metrocal.metrocal.services.Intervention.InterventionService;
import com.metrocal.metrocal.services.Jwt.UserService;
import com.metrocal.metrocal.services.PdfCertificate.PdfCertificateService;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor




public class AdminController {

          private final DemandeService demandeService;

          private final UserService userService;

          private final InterventionService interventionService;

          private final InstrumentService instrumentService;

          private final EtalonService etalonService;

          private final PdfCertificateService pdfCertificateService;


          
 private final DashboardService dashboardService;
    private final DashboardSseEmitterService emitterService;




     @GetMapping("/getDemandes")
    public ResponseEntity<List<DemandeResponseDto>> getAllDemandes() {
    List<DemandeResponseDto> demandes = demandeService.getAllDemandes();
    return ResponseEntity.ok(demandes);
}


@PutMapping("/updateStatutDemande/{id}")
public ResponseEntity<String> updateStatutDemande(
        @PathVariable Long id,
        @RequestParam StatutDemande statut
) {
    try {
        demandeService.updateStatutDemande(id, statut);
        return ResponseEntity.ok("Statut mis à jour avec succès !");
    } catch (NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}



   @PutMapping("/{demandeId}/affecter/{technicienId}")
public ResponseEntity<String> affecterTechnicien(
        @PathVariable Long demandeId,
        @PathVariable Long technicienId) {
    demandeService.affecterTechnicien(demandeId, technicienId);
    return ResponseEntity.ok("Technicien affecté avec succès");
}



@GetMapping("/getTechniciens")
public ResponseEntity<List<UserDto>> getTechniciens() {
    List<UserDto> techniciens = userService.getAllTechniciens();
    return ResponseEntity.ok(techniciens);
}

@GetMapping("/getClients")
public ResponseEntity<List<UserDto>> getClients() {
    List<UserDto> techniciens = userService.getAllClients();
    return ResponseEntity.ok(techniciens);
}



@GetMapping("/getAllInterventions")
    public ResponseEntity<List<InterventionResponseDto>> getAllIntervention() {
        return ResponseEntity.ok(interventionService.getAllInterventions());
    }


    

@GetMapping("/getAllInstruments")
    public ResponseEntity<List<InstrumentResponseDto>> getAllInstruments() {
        return ResponseEntity.ok(instrumentService.getAllInstruments());
    }


    @PostMapping(value = "/addEtalon", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addEtalon(
        @RequestParam String nom,
        @RequestParam String reference,
        @RequestParam String etat,
        @RequestPart(required = false) MultipartFile image) {
        
        try {
            EtalonResponse response = etalonService.createEtalon(nom, reference, etat, image);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



@PutMapping(value = "/updateEtalon/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<EtalonResponse> updateEtalon(
        @PathVariable Long id,
        @RequestParam String nom,
        @RequestParam String reference,
        @RequestParam String etat,
        @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException, java.io.IOException {

    EtalonRequestDto request = new EtalonRequestDto();
    request.setNom(nom);
    request.setReference(reference);
    request.setEtat(EtatEtalon.valueOf(etat));  // Assure conversion propre ici

    EtalonResponse response = etalonService.update(id, request, imageFile);
    return ResponseEntity.ok(response);
}


    @DeleteMapping("/deleteEtalon/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        etalonService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getAllEtalons")
    public ResponseEntity<List<EtalonResponse>> getAll() {
        return ResponseEntity.ok(etalonService.getAll());
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
    

