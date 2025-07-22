package com.kamleads.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadSummaryDto {
    private Long totalLeads;
    private Long activeLeads;
    private Long leadsRequiringCalls;
    private BigDecimal averagePerformanceScore;
}