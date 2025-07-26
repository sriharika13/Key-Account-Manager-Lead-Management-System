package com.kamleads.management.repository;

import com.kamleads.management.enums.InteractionType;
import com.kamleads.management.model.Interaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; // Keep Optional if used for single results, otherwise can remove
import java.util.UUID;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, UUID> {

    // Basic finder methods
    // Derived query for pagination by lead ID, ordered by interaction date descending
    Page<Interaction> findByLeadIdOrderByInteractionDateDesc(UUID leadId, Pageable pageable);

    // Find interactions by KAM ID (basic list, consider pagination if large)
    List<Interaction> findByKamId(UUID kamId);

    Page<Interaction> findByLeadId(UUID leadId,Pageable pageable);

    // Recent interactions for a lead (explicitly named query for clarity)
    @Query(value = "SELECT i FROM Interaction i WHERE i.lead.id = :leadId ORDER BY i.interactionDate DESC",
            name = "Interaction.findRecentInteractionsByLeadId")
    List<Interaction> findRecentInteractionsByLeadId(@Param("leadId") UUID leadId,
                                                     Pageable pageable);

    // Analytics queries

    // Calculate total order value by lead and date range (explicitly named query)
    @Query(value = "SELECT COALESCE(SUM(i.orderValue), 0) FROM Interaction i " +
            "WHERE i.lead.id = :leadId AND i.type = 'ORDER' " +
            "AND i.interactionDate BETWEEN :startDate AND :endDate",
            name = "Interaction.calculateTotalOrderValueByLeadAndDateRange")
    BigDecimal calculateTotalOrderValueByLeadAndDateRange(@Param("leadId") UUID leadId,
                                                          @Param("startDate") LocalDateTime startDate,
                                                          @Param("endDate") LocalDateTime endDate);

    // Find interaction count by KAM and type (explicitly named query)
    @Query(value = "SELECT COUNT(i) FROM Interaction i " +
            "WHERE i.kam.id = :kamId AND i.type = :type " +
            "AND i.interactionDate >= :fromDate",
            name = "Interaction.findInteractionCountByKamAndType")
    Long findInteractionCountByKamAndType(@Param("kamId") UUID kamId,
                                          @Param("type") InteractionType type,
                                          @Param("fromDate") LocalDateTime fromDate);

    // Find average order value by lead (explicitly named query)
    @Query(value = "SELECT COALESCE(AVG(i.orderValue), 0) FROM Interaction i " +
            "WHERE i.lead.id = :leadId AND i.type = 'ORDER' AND i.orderValue > 0",
            name = "Interaction.findAverageOrderValueByLead")
    BigDecimal findAverageOrderValueByLead(@Param("leadId") UUID leadId);

    // KAM performance queries (explicitly named query)
    @Query(value = "SELECT i.kam.id, COUNT(i) as callCount, " +
            "COUNT(CASE WHEN i.type = 'ORDER' THEN 1 END) as orderCount " +
            "FROM Interaction i " +
            "WHERE i.interactionDate >= :fromDate " +
            "GROUP BY i.kam.id",
            name = "Interaction.findKamPerformanceStats")
    List<Object[]> findKamPerformanceStats(@Param("fromDate") LocalDateTime fromDate);

    // Follow-up queries
    // Renamed and ensured consistency for follow-up query (explicitly named)
    @Query(value = "SELECT i FROM Interaction i WHERE i.kam.id = :kamId AND i.followUpDate = :date AND i.status IN ('PENDING', 'RESCHEDULED') ORDER BY i.followUpDate ASC",
            name = "Interaction.findInteractionsRequiringFollowUp")
    List<Interaction> findInteractionsRequiringFollowUp(@Param("kamId") UUID kamId,
                                                        @Param("date") LocalDate date);

    // Derived queries for counts (these usually work well without @Query)
    Long countByLeadIdAndInteractionDateAfter(UUID leadId, LocalDateTime thirtyDaysAgo);
    Long countByLeadIdAndTypeAndInteractionDateAfter(UUID leadId, InteractionType interactionType, LocalDateTime thirtyDaysAgo);

    // This method name implies counting interactions related to a contact's own ID, which seems unusual.
    // If it's meant to count interactions *for* a specific contact, it's fine.
    // If it's meant to count contacts by their own ID, it's redundant with JpaRepository's findById/count.
    // Assuming it means count interactions linked to a contact.
    Integer countByContactId(UUID id);


    // Find interactions by KAM ID and date range (explicitly named query, fixed typo)
    @Query(value = "SELECT i FROM Interaction i WHERE i.kam.id = :kamId AND i.interactionDate BETWEEN :startDate AND :endDate ORDER BY i.interactionDate DESC",
            name = "Interaction.findByKamIdAndDateRange")
    Page<Interaction> findByKamIdAndDateRange(@Param("kamId") UUID kamId, // Fixed typo: removed 'kam'
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate,
                                              Pageable pageable);

    // Calculate total order value by KAM and date range (explicitly named query)
    @Query(value = "SELECT COALESCE(SUM(i.orderValue), 0) FROM Interaction i WHERE i.kam.id = :kamId AND i.type = 'ORDER' AND i.interactionDate BETWEEN :startDate AND :endDate",
            name = "Interaction.calculateTotalOrderValueByKamAndDateRange")
    BigDecimal calculateTotalOrderValueByKamAndDateRange(@Param("kamId") UUID kamId,
                                                         @Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);
}
