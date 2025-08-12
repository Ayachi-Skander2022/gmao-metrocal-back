package com.metrocal.metrocal.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.metrocal.metrocal.entities.Demande;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, Long>{
  List<Demande> findByUser_id(Long userId);

List<Demande> findByTechnicien_Id(Long technicienId);


List<Demande> findByIntervention_Id(Long interventionId);

  @Query("select count(d) from Demande d")
    long countAll();


    @Query("select count(d) from Demande d where d.statutDemande = 'PENDING'")
    long countPending();



    @Query("select count(d) from Demande d where d.statutDemande = 'APPROVED'")
    long countApproved();


    @Query("SELECT d FROM Demande d WHERE d.statutDemande = 'PENDING' ORDER BY d.dateDemande DESC")
List<Demande> findLastDemandes(Pageable pageable);



}
