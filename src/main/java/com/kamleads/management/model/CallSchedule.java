package com.kamleads.management.model;

import com.kamleads.management.enums.CallStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Why separate CallSchedule from Interaction?
 *  * Answer:
 *  * 1. Separation of Concerns: Planning vs Actual execution
 *  * 2. Future Scheduling: Can schedule calls for future dates
 *  * 3. Call Queue Management: Today's pending calls view
 *  * 4. Performance Tracking: Planned vs Actual call metrics
 *  * 5. Rescheduling Logic: Easy to reschedule without affecting history
 */
@Entity
@Table(name = "call_schedule", indexes = {
        @Index(name = "idx_call_schedule_kam_date", columnList = "kam_id, scheduled_date"),
        @Index(name = "idx_call_schedule_status", columnList = "status, scheduled_date"),
        // Index for lead-specific call history
        @Index(name = "idx_call_schedule_lead", columnList = "lead_id, scheduled_date")
})
@Getter
@Setter
public class CallSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kam_id", nullable = false)
    @NotNull(message = "KAM assignment is mandatory for call scheduling")
    private User kam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    @NotNull(message = "Lead reference is mandatory")
    private Lead lead;

    @Column(name = "scheduled_date", nullable = false)
    @NotNull(message = "Scheduled date is required")
    private LocalDate scheduledDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull
    private CallStatus status = CallStatus.PENDING;

    @Column(name = "priority", nullable = false)
    private Integer priority = 3; // Default to medium priority

    @Column(name = "next_scheduled_date")
    private LocalDate nextScheduledDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public CallSchedule() {}

    public CallSchedule(User kam, Lead lead, LocalDate scheduledDate) {
        this.kam = kam;
        this.lead = lead;
        this.scheduledDate = scheduledDate;
        this.status = CallStatus.PENDING;
        this.priority = 3;
    }
    public void reschedule(LocalDate newDate) {
        this.status = CallStatus.RESCHEDULED;
        this.nextScheduledDate = newDate;
    }
    public boolean isOverdue() {
        return scheduledDate.isBefore(LocalDate.now()) && status == CallStatus.PENDING;
    }
}
