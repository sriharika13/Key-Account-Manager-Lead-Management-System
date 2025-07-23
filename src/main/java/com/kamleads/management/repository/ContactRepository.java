package com.kamleads.management.repository;

import com.kamleads.management.model.Contact;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

    // Basic finder methods
    List<Contact> findByLeadId(UUID leadId);
    List<Contact> findByLeadIdAndIsActiveTrue(UUID leadId);
    Optional<Contact> findByLeadIdAndIsPrimaryTrue(UUID leadId);
    Optional<Contact> findByEmail(String email);

    // Search contacts
    @Query("SELECT c FROM Contact c WHERE c.lead.id = :leadId AND " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.role) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Contact> searchContactsByLead(@Param("leadId") UUID leadId,
                                       @Param("searchTerm") String searchTerm);

    // Contact analytics
    @Query("SELECT c.role, COUNT(c) FROM Contact c WHERE c.lead.kam.id = :kamId " +
            "GROUP BY c.role ORDER BY COUNT(c) DESC")
    List<Object[]> findContactRoleDistribution(@Param("kamId") UUID kamId);

    List<Contact> findByLeadIdOrderByNameAsc(UUID id);

    Optional<Contact> findPrimaryContactByLeadId(UUID id);

    Optional<Contact> findByLeadIdAndEmail(UUID id, @Email(message = "Email should be valid") @Size(max = 150, message = "Email must not exceed 150 characters") String email);
}

//@Repository
//public interface ContactRepository extends JpaRepository<Contact, UUID> {
//
//    // Find all contacts for a specific lead
//    List<Contact> findByLeadIdOrderByNameAsc(UUID leadId);
//
//    // Find primary contact for a lead
//    @Query("SELECT c FROM Contact c WHERE c.lead.id = :leadId AND c.isPrimary = true")
//    Optional<Contact> findPrimaryContactByLeadId(@Param("leadId") UUID leadId);
//
//    // Find contact by email
//    Optional<Contact> findByEmail(String email);
//
//    // Check for contact uniqueness per lead
//    Optional<Contact> findByLeadIdAndEmail(UUID leadId, String email);
//
//    // Count contacts for a lead
//    @Query("SELECT COUNT(c) FROM Contact c WHERE c.lead.id = :leadId")
//    Long countContactsByLeadId(@Param("leadId") UUID leadId);
//
//    // Find contacts by role (case-insensitive)
//    List<Contact> findByRoleIgnoreCase(String role);
//}
//
