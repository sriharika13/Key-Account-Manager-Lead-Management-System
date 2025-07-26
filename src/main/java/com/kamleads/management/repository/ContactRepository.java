package com.kamleads.management.repository;

import com.kamleads.management.model.Contact;
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

    // Find primary contact for a lead (using @Query for explicit clarity and robustness)
    @Query("SELECT c FROM Contact c WHERE c.lead.id = :leadId AND c.isPrimary = true")
    Optional<Contact> findPrimaryContactByLeadId(@Param("leadId") UUID leadId);

    Optional<Contact> findByEmail(String email);

    // Find contact by lead ID and email (removed validation annotations from parameters)
    Optional<Contact> findByLeadIdAndEmail(UUID leadId, String email);

    // Search contacts by lead (existing query, looks good)
    @Query("SELECT c FROM Contact c WHERE c.lead.id = :leadId AND " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.role) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Contact> searchContactsByLead(@Param("leadId") UUID leadId,
                                       @Param("searchTerm") String searchTerm);

    // Contact analytics (existing query, looks good)
    @Query("SELECT c.role, COUNT(c) FROM Contact c WHERE c.lead.kam.id = :kamId " +
            "GROUP BY c.role ORDER BY COUNT(c) DESC")
    List<Object[]> findContactRoleDistribution(@Param("kamId") UUID kamId);

    // Find contacts by lead ID, ordered by name (derived query, looks good)
    List<Contact> findByLeadIdOrderByNameAsc(UUID leadId);

    // Count contacts for a lead (added back as it's useful for analytics/summary)
    @Query("SELECT COUNT(c) FROM Contact c WHERE c.lead.id = :leadId")
    Long countContactsByLeadId(@Param("leadId") UUID leadId);
}
