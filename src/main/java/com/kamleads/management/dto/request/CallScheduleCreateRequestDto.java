// CallScheduleCreateRequestDto.java
package com.kamleads.management.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;
public class CallScheduleCreateRequestDto {
    @NotNull(message = "KAM ID is required")
    private UUID kamId;

    @NotNull(message = "Lead ID is required")
    private UUID leadId;

    public void setLeadId(UUID leadId) {
        this.leadId = leadId;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public UUID getLeadId() {
        return leadId;
    }

    public Integer getPriority() {
        return priority;
    }

    @NotNull(message = "Scheduled date is required")
    private LocalDate scheduledDate;

    @Min(value = 1, message = "Priority must be between 1 and 5")
    @Max(value = 5, message = "Priority must be between 1 and 5")
    private Integer priority = 3;

    public UUID getKamId() {
        return kamId;
    }

    public void setKamId(UUID kamId) {
        this.kamId = kamId;
    }
}
