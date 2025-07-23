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
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, UUID> {

    // Basic finder methods
    Page<Interaction> findByLeadId(UUID leadId,Pageable pageable);
    List<Interaction> findByKamId(UUID kamId);
    Page<Interaction> findByLeadIdOrderByInteractionDateDesc(UUID leadId, Pageable pageable);

    // Recent interactions
    @Query("SELECT i FROM Interaction i WHERE i.lead.id = :leadId " +
            "ORDER BY i.interactionDate DESC")
    List<Interaction> findRecentInteractionsByLeadId(@Param("leadId") UUID leadId,
                                                     Pageable pageable);

    // Analytics queries
    @Query("SELECT COALESCE(SUM(i.orderValue), 0) FROM Interaction i " +
            "WHERE i.lead.id = :leadId AND i.type = 'ORDER' " +
            "AND i.interactionDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalOrderValueByLeadAndDateRange(@Param("leadId") UUID leadId,
                                                          @Param("startDate") LocalDateTime startDate,
                                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(i) FROM Interaction i " +
            "WHERE i.kam.id = :kamId AND i.type = :type " +
            "AND i.interactionDate >= :fromDate")
    Long findInteractionCountByKamAndType(@Param("kamId") UUID kamId,
                                          @Param("type") InteractionType type,
                                          @Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT COALESCE(AVG(i.orderValue), 0) FROM Interaction i " +
            "WHERE i.lead.id = :leadId AND i.type = 'ORDER' AND i.orderValue > 0")
    BigDecimal findAverageOrderValueByLead(@Param("leadId") UUID leadId);

    // KAM performance queries
    @Query("SELECT i.kam.id, COUNT(i) as callCount, " +
            "COUNT(CASE WHEN i.type = 'ORDER' THEN 1 END) as orderCount " +
            "FROM Interaction i " +
            "WHERE i.interactionDate >= :fromDate " +
            "GROUP BY i.kam.id")
    List<Object[]> findKamPerformanceStats(@Param("fromDate") LocalDateTime fromDate);

    // Follow-up queries
    @Query("SELECT i FROM Interaction i WHERE i.followUpDate = :date AND i.kam.id = :kamId")
    List<Interaction> findFollowUpsForDate(@Param("kamId") UUID kamId,
                                           @Param("date") LocalDate date);

    Long countByLeadIdAndInteractionDateAfter(UUID leadId, LocalDateTime thirtyDaysAgo);

    Long countByLeadIdAndTypeAndInteractionDateAfter(UUID leadId, InteractionType interactionType, LocalDateTime thirtyDaysAgo);

    Integer countByContactId(UUID id);

    @Query("SELECT i FROM Interaction i WHERE i.kam.id = :kamId AND i.followUpDate = :date")
    List<Interaction> findInteractionsRequiringFollowUp(@Param("kamId") UUID kamId, @Param("date") LocalDate date);


    Page<Interaction> findByKamIdAndDateRange(UUID kamId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    BigDecimal calculateTotalOrderValueByKamAndDateRange(UUID kamId, LocalDateTime startDate, LocalDateTime endDate);
}





//public interface InteractionRepository extends JpaRepository<Interaction, UUID> {
//
//    // Recent interactions for a lead
//    @Query("SELECT i FROM Interaction i WHERE i.lead.id = :leadId ORDER BY i.interactionDate DESC")
//    List<Interaction> findRecentInteractionsByLeadId(@Param("leadId") UUID leadId, Pageable pageable);
//
//    // Total order value for a lead in a date range
//    @Query("SELECT COALESCE(SUM(i.orderValue), 0) FROM Interaction i " +
//            "WHERE i.lead.id = :leadId AND i.type = 'ORDER' " +
//            "AND i.interactionDate BETWEEN :startDate AND :endDate")
//    BigDecimal calculateTotalOrderValueByLeadAndDateRange(
//            @Param("leadId") UUID leadId,
//            @Param("startDate") LocalDateTime startDate,
//            @Param("endDate") LocalDateTime endDate);
//
//    // Interaction count by type and KAM
//    @Query("SELECT COUNT(i) FROM Interaction i WHERE i.kam.id = :kamId AND i.type = :type")
//    Long findInteractionCountByKamAndType(@Param("kamId") UUID kamId, @Param("type") InteractionType type);
//
//    // Average order value for a lead
//    @Query("SELECT COALESCE(AVG(i.orderValue), 0) FROM Interaction i " +
//            "WHERE i.lead.id = :leadId AND i.type = 'ORDER'")
//    BigDecimal findAverageOrderValueByLead(@Param("leadId") UUID leadId);
//
//    // Interactions by KAM in date range
//    @Query("SELECT i FROM Interaction i WHERE i.kam.id = :kamId " +
//            "AND i.interactionDate BETWEEN :startDate AND :endDate " +
//            "ORDER BY i.interactionDate DESC")
//    Page<Interaction> findByKamIdAndDateRange(@Param("kamId") UUID kamId,
//                                              @Param("startDate") LocalDateTime startDate,
//                                              @Param("endDate") LocalDateTime endDate,
//                                              Pageable pageable);
//
//    // Follow-up due interactions
//    @Query("SELECT i FROM Interaction i WHERE i.followUpDate <= :date " +
//            "AND i.kam.id = :kamId ORDER BY i.followUpDate ASC")
//    List<Interaction> findInteractionsRequiringFollowUp(@Param("kamId") UUID kamId,
//                                                        @Param("date") LocalDate date);
//}
