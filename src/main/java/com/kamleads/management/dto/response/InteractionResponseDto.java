package com.kamleads.management.dto.response;

import com.kamleads.management.enums.InteractionStatus;
import com.kamleads.management.enums.InteractionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InteractionResponseDto {
    private UUID id;
    private UUID leadId;
    private String leadName;
    private UUID contactId;
    private String contactName;
    private UUID kamId;
    private String kamName;
    private InteractionType type;
    private InteractionStatus status;
    private LocalDateTime interactionDate;
    private BigDecimal orderValue;
    private LocalDate followUpDate;
    private String notes;
}