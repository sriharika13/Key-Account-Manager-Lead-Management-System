// InteractionCreateRequestDto.java
package com.kamleads.management.dto.request;

import com.kamleads.management.enums.InteractionStatus;
import com.kamleads.management.enums.InteractionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class InteractionCreateRequestDto {
    @NotNull(message = "Lead ID is required")
    private UUID leadId;

    private UUID contactId;

    @NotNull(message = "KAM ID is required")
    private UUID kamId;

    @NotNull(message = "Interaction type is required")
    private InteractionType type;

    @NotNull(message = "Interaction status is required")
    private InteractionStatus status;

    @DecimalMin(value = "0.0", message = "Order value cannot be negative")
    private BigDecimal orderValue;

    private LocalDate followUpDate;

    private String notes;
}
