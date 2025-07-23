package com.kamleads.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentInteractionsSummaryDto {
    private UUID latestInteractionId;
    private String latestInteractionType;
    private LocalDateTime latestInteractionDate;
    private String latestInteractionNotes;
    private Long totalInteractionsLast30Days;
    private Long totalOrdersLast30Days;
    private BigDecimal totalOrderValueLast30Days;
}
