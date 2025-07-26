package com.kamleads.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class LeadSummaryDto {
    private Long totalLeads;
    private Long activeLeads;
    private Long leadsRequiringCalls;
    private BigDecimal averagePerformanceScore;

    public LeadSummaryDto() {
    }

    public LeadSummaryDto(Long totalLeads, Long activeLeads, Long leadsRequiringCalls, BigDecimal averagePerformanceScore) {
        this.totalLeads = totalLeads;
        this.activeLeads = activeLeads;
        this.leadsRequiringCalls = leadsRequiringCalls;
        this.averagePerformanceScore = averagePerformanceScore;
    }

    public Long getTotalLeads() {
        return totalLeads;
    }

    public void setTotalLeads(Long totalLeads) {
        this.totalLeads = totalLeads;
    }

    public Long getActiveLeads() {
        return activeLeads;
    }

    public void setActiveLeads(Long activeLeads) {
        this.activeLeads = activeLeads;
    }

    public Long getLeadsRequiringCalls() {
        return leadsRequiringCalls;
    }

    public void setLeadsRequiringCalls(Long leadsRequiringCalls) {
        this.leadsRequiringCalls = leadsRequiringCalls;
    }

    public BigDecimal getAveragePerformanceScore() {
        return averagePerformanceScore;
    }

    public void setAveragePerformanceScore(BigDecimal averagePerformanceScore) {
        this.averagePerformanceScore = averagePerformanceScore;
    }

}