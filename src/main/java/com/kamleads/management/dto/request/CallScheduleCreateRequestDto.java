// CallScheduleCreateRequestDto.java
package com.kamleads.management.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CallScheduleCreateRequestDto {
    @NotNull(message = "KAM ID is required")
    private UUID kamId;

    @NotNull(message = "Lead ID is required")
    private UUID leadId;

    @NotNull(message = "Scheduled date is required")
    private LocalDate scheduledDate;

    @Min(value = 1, message = "Priority must be between 1 and 5")
    @Max(value = 5, message = "Priority must be between 1 and 5")
    private Integer priority = 3;
}
