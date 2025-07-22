package com.kamleads.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadPerformanceDTO {
    private UUID leadId;
    private String leadName;
    private BigDecimal performanceScore;
    private Long totalInteractions;
    private BigDecimal totalOrderValue;
    private BigDecimal averageOrderValue;
}