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

    public InteractionType getType() {
        return type;
    }

    public void setType(InteractionType type) {
        this.type = type;
    }

    public UUID getLeadId() {
        return leadId;
    }

    public void setLeadId(UUID leadId) {
        this.leadId = leadId;
    }

    public UUID getContactId() {
        return contactId;
    }

    public void setContactId(UUID contactId) {
        this.contactId = contactId;
    }

    public UUID getKamId() {
        return kamId;
    }

    public void setKamId(UUID kamId) {
        this.kamId = kamId;
    }

    public InteractionStatus getStatus() {
        return status;
    }

    public void setStatus(InteractionStatus status) {
        this.status = status;
    }

    public BigDecimal getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(BigDecimal orderValue) {
        this.orderValue = orderValue;
    }

    public LocalDate getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(LocalDate followUpDate) {
        this.followUpDate = followUpDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

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
