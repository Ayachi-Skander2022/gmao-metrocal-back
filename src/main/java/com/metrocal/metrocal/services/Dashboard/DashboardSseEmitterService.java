package com.metrocal.metrocal.services.Dashboard;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.itextpdf.io.exceptions.IOException;
import com.metrocal.metrocal.dto.DashboardStatsDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class DashboardSseEmitterService {
    
     private final Set<SseEmitter> emitters = new CopyOnWriteArraySet<>();

    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(0L); // 0 = no timeout
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        emitters.add(emitter);
        return emitter;
    }

    public void broadcastStats(DashboardStatsDto stats) throws java.io.IOException {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                    .name("dashboard-stats")
                    .data(stats));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}
