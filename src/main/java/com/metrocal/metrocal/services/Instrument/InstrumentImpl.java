package com.metrocal.metrocal.services.Instrument;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.metrocal.metrocal.dto.ClientDemDto;
import com.metrocal.metrocal.dto.InstrumentRequestDto;
import com.metrocal.metrocal.dto.InstrumentResponseDto;

import com.metrocal.metrocal.entities.Instrument;

import com.metrocal.metrocal.entities.User;
import com.metrocal.metrocal.repository.InstrumentRepository;
import com.metrocal.metrocal.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstrumentImpl implements InstrumentService {
    
    
    private final UserRepository userRepository;
    private final InstrumentRepository instrumentRepository;



    public String generateCodeInstrument() {
    long count = instrumentRepository.count() + 1; // nombre total des instruments
    return String.format("INST-%04d", count); // ex: INST-0001
}

    
      @Override
     public InstrumentResponseDto creerInstrument(InstrumentRequestDto instrumentDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // 🔥 email depuis JWT token

        Optional<User> optionalUser = userRepository.findFirstByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            Instrument instrument = new Instrument();
            instrument.setUser(user); // 🔗 Lien avec l'utilisateur
            instrument.setCodeInstrument(generateCodeInstrument()); // ✅ Code auto-généré
            instrument.setNomInstrument(instrumentDto.getNomInstrument());
            instrument.setReferenceInstrument(instrumentDto.getReferenceInstrument());
            instrument.setConstructeur(instrumentDto.getConstructeur());
            instrument.setTypeMesure(instrumentDto.getTypeMesure());
            instrument.setUniteMesure(instrumentDto.getUniteMesure());
            instrument.setMinMesure(instrumentDto.getMinMesure());
            instrument.setMaxMesure(instrumentDto.getMaxMesure());
    
          
            Instrument savedInstrument = instrumentRepository.save(instrument); // 💾

            // 🔄 Mapping vers DTO de réponse
            InstrumentResponseDto responseDto = new InstrumentResponseDto();
            responseDto.setId(savedInstrument.getId());
            responseDto.setCodeInstrument(savedInstrument.getCodeInstrument());
            responseDto.setNomInstrument(savedInstrument.getNomInstrument());
            responseDto.setReferenceInstrument(savedInstrument.getReferenceInstrument());
            responseDto.setConstructeur(savedInstrument.getConstructeur());
            responseDto.setTypeMesure(savedInstrument.getTypeMesure());
            responseDto.setUniteMesure(savedInstrument.getUniteMesure());
            responseDto.setMinMesure(savedInstrument.getMinMesure());
            responseDto.setMaxMesure(savedInstrument.getMaxMesure());

            ClientDemDto clientDto = new ClientDemDto(
                user.getId(),
                user.getFullName(),
                user.getAdresse(),
                user.getTelephone(),
                user.getEmail()
            );
            responseDto.setClient(clientDto); // ✅ user imbriqué

            return responseDto;
        }

        return null;

      }


     @Override
    public List<InstrumentResponseDto> getInstrumentByClientId(Long clientId) {

          List<Instrument> instruments = instrumentRepository.findByUser_id(clientId);

    
  return instruments.stream().map(instrument -> {
        InstrumentResponseDto dto = new InstrumentResponseDto();
        dto.setId(instrument.getId());
        dto.setCodeInstrument(instrument.getCodeInstrument());
        dto.setNomInstrument(instrument.getNomInstrument());
        dto.setReferenceInstrument(instrument.getReferenceInstrument());
        dto.setConstructeur(instrument.getConstructeur());
        dto.setTypeMesure(instrument.getTypeMesure());
        dto.setUniteMesure(instrument.getUniteMesure());
        dto.setMinMesure(instrument.getMinMesure());
        dto.setMaxMesure(instrument.getMaxMesure());
  

        // User (client)
        User user = instrument.getUser();
        if (user != null) {
            dto.setClient(new ClientDemDto(
                user.getId(),
                user.getFullName(),
                user.getAdresse(),
                user.getTelephone(),
                user.getEmail()
            ));
        }

  

        return dto;
    }).collect(Collectors.toList());


    }


      @Override
     public List<InstrumentResponseDto> getAllInstruments(){
          List<Instrument> instruments = instrumentRepository.findAll();

    return instruments.stream().map(instrument -> {
        InstrumentResponseDto dto = new InstrumentResponseDto();
        dto.setId(instrument.getId());
        dto.setCodeInstrument(instrument.getCodeInstrument());
        dto.setNomInstrument(instrument.getNomInstrument());
        dto.setReferenceInstrument(instrument.getReferenceInstrument());
        dto.setConstructeur(instrument.getConstructeur());
        dto.setTypeMesure(instrument.getTypeMesure());
        dto.setUniteMesure(instrument.getUniteMesure());
        dto.setMinMesure(instrument.getMinMesure());
        dto.setMaxMesure(instrument.getMaxMesure());


        // User (client)
        User user = instrument.getUser();
        if (user != null) {
            dto.setClient(new ClientDemDto(
                user.getId(),
                user.getFullName(),
                user.getAdresse(),
                user.getTelephone(),
                user.getEmail()
            ));
        }


        return dto;
    }).collect(Collectors.toList());

     }

    

    

}
