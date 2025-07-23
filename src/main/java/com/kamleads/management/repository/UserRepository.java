package com.kamleads.management.repository;

import com.kamleads.management.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Basic finder methods
    Optional<User> findByEmail(String email);
    List<User> findByNameContainingIgnoreCase(String name);

    // Custom query for active KAMs with lead count
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.leads l WHERE u.id = :kamId")
    Optional<User> findByIdWithLeads(@Param("kamId") UUID kamId);

    // Find KAMs by timezone for scheduling optimization
    @Query("SELECT u FROM User u WHERE u.timezone = :timezone")
    List<User> findByTimezone(@Param("timezone") String timezone);

    // Performance query - KAMs with lead counts
    @Query("SELECT u, COUNT(l) as leadCount FROM User u " +
            "LEFT JOIN u.leads l " +
            "WHERE l.status IN ('NEW', 'CONTACTED', 'INTERESTED', 'NEGOTIATING') " +
            "GROUP BY u " +
            "ORDER BY leadCount DESC")
    List<Object[]> findKamsWithActiveLeadCounts();

    boolean existsByEmail(@NotBlank(message = "Email is required") @Email(message = "Email should be valid") @Size(max = 150, message = "Email must not exceed 150 characters") String email);

    Page<User> findKamsWithLeads(Pageable pageable);  //?????
}


//public interface UserRepository extends JpaRepository<User, UUID> {
//    Optional<User> findByEmail(String email);
//    boolean existsByEmail(String email);
//
//    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.leads l WHERE SIZE(u.leads) > 0")
//    Page<User> findKamsWithLeads(Pageable pageable);
//
//    @Query("SELECT u FROM User u WHERE u.timezone = :timezone")
//    List<User> findByTimezone(@Param("timezone") String timezone);
//}
