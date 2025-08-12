package com.metrocal.metrocal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.metrocal.metrocal.entities.Instrument;


@Repository
public interface InstrumentRepository extends JpaRepository <Instrument, Long>{
    List<Instrument> findByUser_id(Long userId);

}
