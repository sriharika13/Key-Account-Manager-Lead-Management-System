package com.kamleads.management.repository;

import com.kamleads.management.enums.LeadStatus;
import com.kamleads.management.model.Lead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface LeadRepository extends JpaRepository<Lead, UUID>, LeadRepositoryCustom {

    // Basic finder methods
    List<Lead> findByKamId(UUID kamId);
    List<Lead> findByStatus(LeadStatus status);
    List<Lead> findByCity(String city);
    Page<Lead> findByKamIdAndStatus(UUID kamId, LeadStatus status, Pageable pageable);

    // Status-based queries
    @Query("SELECT l FROM Lead l WHERE l.kam.id = :kamId AND l.status IN :statuses")
    List<Lead> findByKamIdAndStatusIn(@Param("kamId") UUID kamId,
                                      @Param("statuses") List<LeadStatus> statuses);

    // Performance-based queries
    @Query("SELECT l FROM Lead l WHERE l.kam.id = :kamId AND l.performanceScore >= :minScore " +
            "ORDER BY l.performanceScore DESC")
    List<Lead> findTopPerformingLeads(@Param("kamId") UUID kamId,
                                      @Param("minScore") Double minScore,
                                      Pageable pageable);

    // Call scheduling queries
    // FIXED: Changed date arithmetic to use a JPQL-compatible function (DATE_ADD)
    @Query("SELECT l FROM Lead l WHERE l.kam.id = :kamId " +
            "AND l.status NOT IN ('CLOSED_WON', 'CLOSED_LOST') " +
            "AND (l.lastCallDate IS NULL " +
            "OR FUNCTION('DATE_ADD', l.lastCallDate, l.callFrequency, 'DAY') <= :today)")
    List<Lead> findLeadsRequiringCallsToday(@Param("kamId") UUID kamId,
                                            @Param("today") LocalDate today);

    // City and cuisine analytics
    @Query("SELECT l.city, COUNT(l), AVG(l.performanceScore) FROM Lead l " +
            "WHERE l.kam.id = :kamId " +
            "GROUP BY l.city " +
            "ORDER BY COUNT(l) DESC")
    List<Object[]> findLeadDistributionByCity(@Param("kamId") UUID kamId);

    // No need to declare findLeadsWithFilters here, it's in LeadRepositoryCustom
}
