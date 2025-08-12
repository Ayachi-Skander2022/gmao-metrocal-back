package com.metrocal.metrocal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.metrocal.metrocal.entities.InterventionPlus;

@Repository
public interface InterventionRepository extends JpaRepository <InterventionPlus, Long>{

    List<InterventionPlus> findByTechnicien_id(Long techId);
    List<InterventionPlus> findByDemande_id(Long demandeId);

List<InterventionPlus> findByDemande_User_Id(Long clientId);


    boolean existsByDemande_Id(Long demandeId);


     @Query("select count(i) from InterventionPlus i")
    long countAll();
  
    
}
