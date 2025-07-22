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

@Data
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
}