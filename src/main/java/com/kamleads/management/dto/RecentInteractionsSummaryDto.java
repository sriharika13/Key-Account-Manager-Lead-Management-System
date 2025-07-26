package com.kamleads.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class RecentInteractionsSummaryDto {
    private UUID latestInteractionId;
    private String latestInteractionType;
    private LocalDateTime latestInteractionDate;
    private String latestInteractionNotes;
    private Long totalInteractionsLast30Days;
    private Long totalOrdersLast30Days;

    public UUID getLatestInteractionId() {
        return latestInteractionId;
    }

    public void setLatestInteractionId(UUID latestInteractionId) {
        this.latestInteractionId = latestInteractionId;
    }

    public BigDecimal getTotalOrderValueLast30Days() {
        return totalOrderValueLast30Days;
    }

    public void setTotalOrderValueLast30Days(BigDecimal totalOrderValueLast30Days) {
        this.totalOrderValueLast30Days = totalOrderValueLast30Days;
    }

    public Long getTotalOrdersLast30Days() {
        return totalOrdersLast30Days;
    }

    public void setTotalOrdersLast30Days(Long totalOrdersLast30Days) {
        this.totalOrdersLast30Days = totalOrdersLast30Days;
    }

    public Long getTotalInteractionsLast30Days() {
        return totalInteractionsLast30Days;
    }

    public void setTotalInteractionsLast30Days(Long totalInteractionsLast30Days) {
        this.totalInteractionsLast30Days = totalInteractionsLast30Days;
    }

    public String getLatestInteractionNotes() {
        return latestInteractionNotes;
    }

    public void setLatestInteractionNotes(String latestInteractionNotes) {
        this.latestInteractionNotes = latestInteractionNotes;
    }

    public LocalDateTime getLatestInteractionDate() {
        return latestInteractionDate;
    }

    public void setLatestInteractionDate(LocalDateTime latestInteractionDate) {
        this.latestInteractionDate = latestInteractionDate;
    }

    public String getLatestInteractionType() {
        return latestInteractionType;
    }

    public void setLatestInteractionType(String latestInteractionType) {
        this.latestInteractionType = latestInteractionType;
    }

    private BigDecimal totalOrderValueLast30Days;

}
