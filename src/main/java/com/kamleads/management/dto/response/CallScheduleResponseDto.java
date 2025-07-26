package com.kamleads.management.dto.response;

import com.kamleads.management.enums.CallStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
public class CallScheduleResponseDto {
    private UUID id;
    private UUID kamId;
    private String kamName;
    private UUID leadId;
    private String leadName;
    private String leadCity;
    private LocalDate scheduledDate;
    private CallStatus status;
    private Integer priority;
    private LocalDate nextScheduledDate;

    public Boolean getOverdue() {
        return isOverdue;
    }

    public void setOverdue(Boolean overdue) {
        isOverdue = overdue;
    }

    public LocalDate getNextScheduledDate() {
        return nextScheduledDate;
    }

    public void setNextScheduledDate(LocalDate nextScheduledDate) {
        this.nextScheduledDate = nextScheduledDate;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public CallStatus getStatus() {
        return status;
    }

    public void setStatus(CallStatus status) {
        this.status = status;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getLeadCity() {
        return leadCity;
    }

    public void setLeadCity(String leadCity) {
        this.leadCity = leadCity;
    }

    public String getLeadName() {
        return leadName;
    }

    public void setLeadName(String leadName) {
        this.leadName = leadName;
    }

    public UUID getLeadId() {
        return leadId;
    }

    public void setLeadId(UUID leadId) {
        this.leadId = leadId;
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    private Boolean isOverdue;


}