package com.kamleads.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

public class LeadPerformanceDTO {
    private UUID leadId;
    private String leadName;
    private BigDecimal performanceScore;
    private Long totalInteractions;
    private BigDecimal totalOrderValue;
    private BigDecimal averageOrderValue;

    public LeadPerformanceDTO(UUID leadId, String leadName, BigDecimal performanceScore, Long totalInteractions, BigDecimal totalOrderValue, BigDecimal averageOrderValue) {
        this.leadId = leadId;
        this.leadName = leadName;
        this.performanceScore = performanceScore;
        this.totalInteractions = totalInteractions;
        this.totalOrderValue = totalOrderValue;
        this.averageOrderValue = averageOrderValue;
    }

    public LeadPerformanceDTO() {
    }

    public UUID getLeadId() {
        return leadId;
    }

    public void setLeadId(UUID leadId) {
        this.leadId = leadId;
    }

    public String getLeadName() {
        return leadName;
    }

    public void setLeadName(String leadName) {
        this.leadName = leadName;
    }

    public BigDecimal getPerformanceScore() {
        return performanceScore;
    }

    public void setPerformanceScore(BigDecimal performanceScore) {
        this.performanceScore = performanceScore;
    }

    public Long getTotalInteractions() {
        return totalInteractions;
    }

    public void setTotalInteractions(Long totalInteractions) {
        this.totalInteractions = totalInteractions;
    }

    public BigDecimal getTotalOrderValue() {
        return totalOrderValue;
    }

    public void setTotalOrderValue(BigDecimal totalOrderValue) {
        this.totalOrderValue = totalOrderValue;
    }

    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }
}