package com.metrocal.metrocal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.metrocal.metrocal.entities.User;
import com.metrocal.metrocal.entities.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findFirstByEmail(String email);
    Optional<User> findByUserRole(UserRole userRole);

    List<User> findTechByUserRole(UserRole role);


    
    @Query("select count(c) from User c where c.userRole = 'CLIENT'")
    long countClients();

}
