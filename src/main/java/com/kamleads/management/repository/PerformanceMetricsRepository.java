package com.kamleads.management.repository;

import com.kamleads.management.model.PerformanceMetrics;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PerformanceMetricsRepository extends JpaRepository<PerformanceMetrics, UUID> {

    // Latest metrics by lead
    @Query("SELECT pm FROM PerformanceMetrics pm WHERE pm.lead.id = :leadId ORDER BY pm.metricDate DESC")
    List<PerformanceMetrics> findLatestMetricsByLeadId(@Param("leadId") UUID leadId);

    // Metrics by lead in date range
    @Query("SELECT pm FROM PerformanceMetrics pm WHERE pm.lead.id = :leadId " +
            "AND pm.metricDate BETWEEN :startDate AND :endDate ORDER BY pm.metricDate DESC")
    List<PerformanceMetrics> findMetricsByLeadAndDateRange(@Param("leadId") UUID leadId,
                                                           @Param("startDate") LocalDate startDate,
                                                           @Param("endDate") LocalDate endDate);

    // Metric for specific lead and date
    Optional<PerformanceMetrics> findByLeadIdAndMetricDate(UUID leadId, LocalDate metricDate);

    // Aggregated metrics for KAM’s leads
    @Query("SELECT pm FROM PerformanceMetrics pm " +
            "WHERE pm.lead.kam.id = :kamId " +
            "AND pm.metricDate BETWEEN :startDate AND :endDate ORDER BY pm.metricValue DESC")
    List<PerformanceMetrics> findMetricsByKamAndDateRange(@Param("kamId") UUID kamId,
                                                          @Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);
}
