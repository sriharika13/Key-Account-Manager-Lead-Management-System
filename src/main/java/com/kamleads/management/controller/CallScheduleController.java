package com.kamleads.management.controller;

import com.kamleads.management.dto.request.CallScheduleCreateRequestDto;
import com.kamleads.management.dto.response.CallScheduleResponseDto;
import com.kamleads.management.exception.ResourceNotFoundException;
import com.kamleads.management.service.CallScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/call-schedules")
public class CallScheduleController {

    private final CallScheduleService callScheduleService;

    @Autowired
    public CallScheduleController(CallScheduleService callScheduleService) {
        this.callScheduleService = callScheduleService;
    }

    /**
     * Creates a new call schedule.
     * Accessible by 'KAM' role.
     */
    @PostMapping
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<CallScheduleResponseDto> createCallSchedule(@Valid @RequestBody CallScheduleCreateRequestDto requestDto) {
        CallScheduleResponseDto createdSchedule = callScheduleService.createCallSchedule(requestDto);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    /**
     * Retrieves a call schedule by ID.
     * Accessible by 'KAM' role.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<CallScheduleResponseDto> getCallScheduleById(@PathVariable UUID id) {
        CallScheduleResponseDto schedule = callScheduleService.getCallScheduleById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Call schedule not found with ID: " + id));
        return ResponseEntity.ok(schedule);
    }

    /**
     * Retrieves scheduled calls for a specific KAM on a given date.
     * Accessible by 'KAM' role.
     */
    @GetMapping("/by-kam/{kamId}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<List<CallScheduleResponseDto>> getScheduledCallsForKamAndDate(
            @PathVariable UUID kamId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<CallScheduleResponseDto> schedules = callScheduleService.getScheduledCallsForKamAndDate(kamId, date);
        return ResponseEntity.ok(schedules);
    }

    /**
     * Retrieves overdue calls for a specific KAM.
     * Accessible by 'KAM' role.
     */
    @GetMapping("/overdue/{kamId}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<List<CallScheduleResponseDto>> getOverdueCallsForKam(@PathVariable UUID kamId) {
        List<CallScheduleResponseDto> overdueCalls = callScheduleService.getOverdueCallsForKam(kamId);
        return ResponseEntity.ok(overdueCalls);
    }

    /**
     * Retrieves upcoming calls for a specific lead.
     * Accessible by 'KAM' role.
     */
    @GetMapping("/upcoming-by-lead/{leadId}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<List<CallScheduleResponseDto>> getUpcomingCallsForLead(
            @PathVariable UUID leadId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate) {
        List<CallScheduleResponseDto> upcomingCalls = callScheduleService.getUpcomingCallsForLead(leadId, fromDate);
        return ResponseEntity.ok(upcomingCalls);
    }

    /**
     * Updates an existing call schedule.
     * Accessible by 'KAM' role.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<CallScheduleResponseDto> updateCallSchedule(@PathVariable UUID id, @Valid @RequestBody CallScheduleCreateRequestDto requestDto) {
        CallScheduleResponseDto updatedSchedule = callScheduleService.updateCallSchedule(id, requestDto);
        return ResponseEntity.ok(updatedSchedule);
    }

    /**
     * Marks a call schedule as completed.
     * Accessible by 'KAM' role.
     */
    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<CallScheduleResponseDto> completeCall(@PathVariable UUID id) {
        CallScheduleResponseDto completedSchedule = callScheduleService.completeCall(id);
        return ResponseEntity.ok(completedSchedule);
    }

    /**
     * Marks a call schedule as missed.
     * Accessible by 'KAM' role.
     */
    @PatchMapping("/{id}/miss")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<CallScheduleResponseDto> missCall(@PathVariable UUID id) {
        CallScheduleResponseDto missedSchedule = callScheduleService.missCall(id);
        return ResponseEntity.ok(missedSchedule);
    }

    /**
     * Reschedules a call.
     * Accessible by 'KAM' role.
     */
    @PatchMapping("/{id}/reschedule")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<CallScheduleResponseDto> rescheduleCall(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newScheduledDate) {
        CallScheduleResponseDto rescheduled = callScheduleService.rescheduleCall(id, newScheduledDate);
        return ResponseEntity.ok(rescheduled);
    }

    /**
     * Cancels a call schedule.
     * Accessible by 'KAM' role.
     */
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<CallScheduleResponseDto> cancelCall(@PathVariable UUID id) {
        CallScheduleResponseDto cancelled = callScheduleService.cancelCall(id);
        return ResponseEntity.ok(cancelled);
    }

    /**
     * Deletes a call schedule.
     * Accessible by 'KAM' role.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<Void> deleteCallSchedule(@PathVariable UUID id) {
        callScheduleService.deleteCallSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
