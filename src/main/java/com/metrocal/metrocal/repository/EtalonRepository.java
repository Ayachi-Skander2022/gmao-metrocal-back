package com.metrocal.metrocal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.metrocal.metrocal.entities.Etalon;

@Repository
public interface EtalonRepository extends JpaRepository<Etalon, Long>{
    
}
