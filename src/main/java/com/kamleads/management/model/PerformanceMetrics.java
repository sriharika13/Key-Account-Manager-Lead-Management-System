package com.kamleads.management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Why separate table for metrics instead of calculating on-the-fly?
 *  * Answer:
 *  * 1. Performance: Pre-calculated values avoid complex aggregation queries
 *  * 2. Historical Data: Maintains metrics history even if source data changes
 *  * 3. Reporting Speed: Dashboard loads faster with pre-computed metrics
 *  * 4. Trend Analysis: Easy to compare metrics across different time periods
 *  * 5. Data Consistency: Consistent metrics even if calculation logic changes
 */
@Entity
@Table(name = "performance_metrics", indexes = {
        // Index for lead-specific performance tracking
        @Index(name = "idx_performance_lead_date", columnList = "lead_id, metric_date DESC"),
        // Index for metric type filtering
        @Index(name = "idx_performance_date", columnList = "metric_date DESC")
})
@Getter
@Setter
public class PerformanceMetrics {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private Lead lead;

    /**
     * Metric Date - Date for which this metric is calculated
     *
     * Usually calculated daily, weekly, or monthly
     * LocalDate: No time component needed for aggregated metrics
     */
    @Column(name = "metric_date", nullable = false)
    @NotNull(message = "Metric date is required")
    private LocalDate metricDate;

/**
 * Metric Value - The calculated performance value
 *
 * BigDecimal: Used for precise decimal calculations
 * - Currency values (order amounts)
 * - Percentages (conversion rates)
 * - Counts (number of calls)
 * */
    @Column(name = "metric_value", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Metric value is required")
    private BigDecimal metricValue;

    /**
     * Target Value - Expected/goal value for this metric (optional)
     *
     * Used for performance comparison and goal tracking
     * Example: Target 50 calls per day, actual 45 calls
     */
    @Column(name = "target_value", precision = 15, scale = 2)
    private BigDecimal targetValue;

    /**
     * Period Type - Time period this metric covers
     *
     * Examples: DAILY, WEEKLY, MONTHLY, QUARTERLY
     * Helps in aggregation and reporting
     */
    @Column(name = "period_type", length = 20)
    private String periodType = "DAILY";

    @Column(name = "calculated_at", nullable = false)
    @NotNull
    private LocalDateTime calculatedAt = LocalDateTime.now();

    public PerformanceMetrics() {}

    public PerformanceMetrics(Lead lead,  BigDecimal metricValue, LocalDate metricDate) {
        this.lead = lead;
        this.metricValue = metricValue;
        this.metricDate = metricDate;
        this.calculatedAt = LocalDateTime.now();
    }
    public boolean meetsTarget() {
        if (targetValue == null) return true; // No target set
        return metricValue.compareTo(targetValue) >= 0;
    }

    public BigDecimal getTargetAchievementPercentage() {
        if (targetValue == null || targetValue.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        return metricValue
                .multiply(BigDecimal.valueOf(100))
                .divide(targetValue, 2, BigDecimal.ROUND_HALF_UP);
    }

    public void updateValue(BigDecimal newValue) {
        this.metricValue = newValue;
        this.calculatedAt = LocalDateTime.now();
    }

}
