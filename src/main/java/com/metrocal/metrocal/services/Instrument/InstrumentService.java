package com.metrocal.metrocal.services.Instrument;

import java.util.List;


import com.metrocal.metrocal.dto.InstrumentRequestDto;
import com.metrocal.metrocal.dto.InstrumentResponseDto;

public interface InstrumentService {

    
      InstrumentResponseDto creerInstrument(InstrumentRequestDto instrumentDto) ;

      List<InstrumentResponseDto> getAllInstruments();


     List<InstrumentResponseDto> getInstrumentByClientId(Long clientId);


}
