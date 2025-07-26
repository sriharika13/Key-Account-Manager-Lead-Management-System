package com.kamleads.management.dto.response;

import com.kamleads.management.dto.ContactSummaryDto;
import com.kamleads.management.dto.RecentInteractionsSummaryDto;
import com.kamleads.management.enums.LeadStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
public class LeadResponseDto {
    private UUID id;
    private String name;
    private String city;
    private String cuisineType;
    private LeadStatus status;
    private String kamName;
    private UUID kamId;
    private Integer callFrequency;
    private LocalDate lastCallDate;
    private LocalDate nextCallDate;
    private BigDecimal performanceScore;
    private Boolean requiresCallToday;
    private Integer totalContacts;
    private List<ContactSummaryDto> contacts;
    private RecentInteractionsSummaryDto recentActivity;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public LeadStatus getStatus() {
        return status;
    }

    public void setStatus(LeadStatus status) {
        this.status = status;
    }

    public String getKamName() {
        return kamName;
    }

    public void setKamName(String kamName) {
        this.kamName = kamName;
    }

    public UUID getKamId() {
        return kamId;
    }

    public void setKamId(UUID kamId) {
        this.kamId = kamId;
    }

    public Integer getCallFrequency() {
        return callFrequency;
    }

    public void setCallFrequency(Integer callFrequency) {
        this.callFrequency = callFrequency;
    }

    public LocalDate getLastCallDate() {
        return lastCallDate;
    }

    public void setLastCallDate(LocalDate lastCallDate) {
        this.lastCallDate = lastCallDate;
    }

    public LocalDate getNextCallDate() {
        return nextCallDate;
    }

    public void setNextCallDate(LocalDate nextCallDate) {
        this.nextCallDate = nextCallDate;
    }

    public BigDecimal getPerformanceScore() {
        return performanceScore;
    }

    public void setPerformanceScore(BigDecimal performanceScore) {
        this.performanceScore = performanceScore;
    }

    public Boolean getRequiresCallToday() {
        return requiresCallToday;
    }

    public void setRequiresCallToday(Boolean requiresCallToday) {
        this.requiresCallToday = requiresCallToday;
    }

    public Integer getTotalContacts() {
        return totalContacts;
    }

    public void setTotalContacts(Integer totalContacts) {
        this.totalContacts = totalContacts;
    }

    public List<ContactSummaryDto> getContacts() {
        return contacts;
    }

    public void setContacts(List<ContactSummaryDto> contacts) {
        this.contacts = contacts;
    }

    public RecentInteractionsSummaryDto getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(RecentInteractionsSummaryDto recentActivity) {
        this.recentActivity = recentActivity;
    }
}