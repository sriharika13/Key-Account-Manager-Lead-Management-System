package com.kamleads.management.dto.response;

import com.kamleads.management.enums.CallStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
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
    private Boolean isOverdue;
}