package com.kamleads.management.service;

import com.kamleads.management.dto.request.CallScheduleCreateRequestDto;
import com.kamleads.management.dto.response.CallScheduleResponseDto;
import com.kamleads.management.enums.CallStatus;
import com.kamleads.management.model.CallSchedule;
import com.kamleads.management.model.Lead;
import com.kamleads.management.model.User;
import com.kamleads.management.repository.CallScheduleRepository;
import com.kamleads.management.repository.LeadRepository;
import com.kamleads.management.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CallScheduleService {

    public CallScheduleRepository getCallScheduleRepository() {
        return callScheduleRepository;
    }

    public LeadRepository getLeadRepository() {
        return leadRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    private final CallScheduleRepository callScheduleRepository;
    private final LeadRepository leadRepository;
    private final UserRepository userRepository;

    @Autowired
    public CallScheduleService(CallScheduleRepository callScheduleRepository,
                               LeadRepository leadRepository,
                               UserRepository userRepository) {
        this.callScheduleRepository = callScheduleRepository;
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new call schedule entry.
     *
     * @param requestDto The DTO containing call schedule details.
     * @return CallScheduleResponseDto of the created schedule.
     * @throws RuntimeException if KAM or Lead not found.
     */
    @Transactional
    public CallScheduleResponseDto createCallSchedule(CallScheduleCreateRequestDto requestDto) {
        User kam = userRepository.findById(requestDto.getKamId())
                .orElseThrow(() -> new RuntimeException("KAM not found with ID: " + requestDto.getKamId()));

        Lead lead = leadRepository.findById(requestDto.getLeadId())
                .orElseThrow(() -> new RuntimeException("Lead not found with ID: " + requestDto.getLeadId()));

        CallSchedule callSchedule = new CallSchedule();
        callSchedule.setId(UUID.randomUUID());
        callSchedule.setKam(kam);
        callSchedule.setLead(lead);
        callSchedule.setScheduledDate(requestDto.getScheduledDate());
        callSchedule.setStatus(CallStatus.PENDING); // New calls are always pending
        callSchedule.setPriority(requestDto.getPriority());
        // nextScheduledDate is usually set when rescheduling or completing a call
        callSchedule.setNextScheduledDate(null);

        CallSchedule savedCallSchedule = callScheduleRepository.save(callSchedule);
        return mapToCallScheduleResponseDto(savedCallSchedule);
    }

    /**
     * Retrieves a call schedule entry by its ID.
     *
     * @param id The UUID of the call schedule.
     * @return Optional<CallScheduleResponseDto> if found, empty otherwise.
     */
    @Transactional(readOnly = true)
    public Optional<CallScheduleResponseDto> getCallScheduleById(UUID id) {
        return callScheduleRepository.findById(id).map(this::mapToCallScheduleResponseDto);
    }

    /**
     * Retrieves scheduled calls for a specific KAM on a given date.
     *
     * @param kamId The UUID of the KAM.
     * @param date The scheduled date.
     * @return List of CallScheduleResponseDto.
     * @throws RuntimeException if KAM not found.
     */
    @Transactional(readOnly = true)
    public List<CallScheduleResponseDto> getScheduledCallsForKamAndDate(UUID kamId, LocalDate date) {
        if (!userRepository.existsById(kamId)) {
            throw new RuntimeException("KAM not found with ID: " + kamId);
        }
        return callScheduleRepository.findScheduledCallsForKamAndDate(kamId, date).stream()
                .map(this::mapToCallScheduleResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves overdue calls for a specific KAM.
     *
     * @param kamId The UUID of the KAM.
     * @return List of CallScheduleResponseDto.
     * @throws RuntimeException if KAM not found.
     */
    @Transactional(readOnly = true)
    public List<CallScheduleResponseDto> getOverdueCallsForKam(UUID kamId) {
        if (!userRepository.existsById(kamId)) {
            throw new RuntimeException("KAM not found with ID: " + kamId);
        }
        return callScheduleRepository.findOverdueCallsForKam(kamId, LocalDate.now()).stream()
                .map(this::mapToCallScheduleResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves upcoming calls for a specific lead.
     *
     * @param leadId The UUID of the lead.
     * @param fromDate The date from which to look for upcoming calls.
     * @return List of CallScheduleResponseDto.
     * @throws RuntimeException if lead not found.
     */
    @Transactional(readOnly = true)
    public List<CallScheduleResponseDto> getUpcomingCallsForLead(UUID leadId, LocalDate fromDate) {
        if (!leadRepository.existsById(leadId)) {
            throw new RuntimeException("Lead not found with ID: " + leadId);
        }
        return callScheduleRepository.findUpcomingCallsForLead(leadId, fromDate).stream()
                .map(this::mapToCallScheduleResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing call schedule entry.
     *
     * @param id The UUID of the call schedule to update.
     * @param requestDto The DTO containing updated call schedule details.
     * @return CallScheduleResponseDto of the updated schedule.
     * @throws RuntimeException if call schedule, KAM, or Lead not found.
     */
    @Transactional
    public CallScheduleResponseDto updateCallSchedule(UUID id, CallScheduleCreateRequestDto requestDto) {
        CallSchedule callSchedule = callScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Call schedule not found with ID: " + id));

        User kam = userRepository.findById(requestDto.getKamId())
                .orElseThrow(() -> new RuntimeException("KAM not found with ID: " + requestDto.getKamId()));

        Lead lead = leadRepository.findById(requestDto.getLeadId())
                .orElseThrow(() -> new RuntimeException("Lead not found with ID: " + requestDto.getLeadId()));

        // Prevent changing KAM or Lead for an existing schedule (usually not allowed)
        if (!callSchedule.getKam().getId().equals(kam.getId())) {
            throw new IllegalArgumentException("Cannot change KAM for an existing call schedule.");
        }
        if (!callSchedule.getLead().getId().equals(lead.getId())) {
            throw new IllegalArgumentException("Cannot change Lead for an existing call schedule.");
        }

        callSchedule.setScheduledDate(requestDto.getScheduledDate());
        callSchedule.setPriority(requestDto.getPriority());
        // Status and nextScheduledDate are typically updated via specific methods (e.g., completeCall, rescheduleCall)

        CallSchedule updatedCallSchedule = callScheduleRepository.save(callSchedule);
        return mapToCallScheduleResponseDto(updatedCallSchedule);
    }

    /**
     * Marks a call schedule as completed.
     *
     * @param id The UUID of the call schedule.
     * @return CallScheduleResponseDto of the updated schedule.
     * @throws RuntimeException if call schedule not found or already completed/cancelled.
     */
    @Transactional
    public CallScheduleResponseDto completeCall(UUID id) {
        CallSchedule callSchedule = callScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Call schedule not found with ID: " + id));

        if (CallStatus.COMPLETED.equals(callSchedule.getStatus()) || CallStatus.CANCELLED.equals(callSchedule.getStatus())) {
            throw new IllegalStateException("Call is already completed or cancelled.");
        }

        callSchedule.setStatus(CallStatus.COMPLETED);
        // When a call is completed, the next scheduled date might be derived from lead's call frequency
        Lead lead = callSchedule.getLead();
        if (lead != null && lead.getCallFrequency() != null) {
            callSchedule.setNextScheduledDate(LocalDate.now().plusDays(lead.getCallFrequency()));
        }

        CallSchedule updatedCallSchedule = callScheduleRepository.save(callSchedule);
        return mapToCallScheduleResponseDto(updatedCallSchedule);
    }

    /**
     * Marks a call schedule as missed.
     *
     * @param id The UUID of the call schedule.
     * @return CallScheduleResponseDto of the updated schedule.
     * @throws RuntimeException if call schedule not found or already completed/cancelled.
     */
    @Transactional
    public CallScheduleResponseDto missCall(UUID id) {
        CallSchedule callSchedule = callScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Call schedule not found with ID: " + id));

        if (CallStatus.COMPLETED.equals(callSchedule.getStatus()) || CallStatus.CANCELLED.equals(callSchedule.getStatus())) {
            throw new IllegalStateException("Call is already completed or cancelled.");
        }

        callSchedule.setStatus(CallStatus.NO_ANSWER);
        // A missed call might trigger an immediate reschedule or be handled by a separate process
        // For simplicity, no nextScheduledDate is set here, but could be.
        CallSchedule updatedCallSchedule = callScheduleRepository.save(callSchedule);
        return mapToCallScheduleResponseDto(updatedCallSchedule);
    }

    /**
     * Reschedules a call.
     *
     * @param id The UUID of the call schedule.
     * @param newScheduledDate The new date for the call.
     * @return CallScheduleResponseDto of the updated schedule.
     * @throws RuntimeException if call schedule not found or new date is in the past.
     */
    @Transactional
    public CallScheduleResponseDto rescheduleCall(UUID id, LocalDate newScheduledDate) {
        CallSchedule callSchedule = callScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Call schedule not found with ID: " + id));

        if (newScheduledDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("New scheduled date cannot be in the past.");
        }

        callSchedule.setScheduledDate(newScheduledDate);
        callSchedule.setStatus(CallStatus.RESCHEDULED); // Or PENDING, depending on desired workflow
        callSchedule.setNextScheduledDate(newScheduledDate); // Next scheduled date is the new scheduled date

        CallSchedule updatedCallSchedule = callScheduleRepository.save(callSchedule);
        return mapToCallScheduleResponseDto(updatedCallSchedule);
    }

    /**
     * Cancels a call schedule.
     *
     * @param id The UUID of the call schedule.
     * @return CallScheduleResponseDto of the updated schedule.
     * @throws RuntimeException if call schedule not found.
     */
    @Transactional
    public CallScheduleResponseDto cancelCall(UUID id) {
        CallSchedule callSchedule = callScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Call schedule not found with ID: " + id));
        callSchedule.setStatus(CallStatus.CANCELLED);
        callSchedule.setNextScheduledDate(null); // No next scheduled date if cancelled
        CallSchedule updatedCallSchedule = callScheduleRepository.save(callSchedule);
        return mapToCallScheduleResponseDto(updatedCallSchedule);
    }

    /**
     * Deletes a call schedule entry by its ID.
     *
     * @param id The UUID of the call schedule to delete.
     * @throws RuntimeException if call schedule not found.
     */
    @Transactional
    public void deleteCallSchedule(UUID id) {
        if (!callScheduleRepository.existsById(id)) {
            throw new RuntimeException("Call schedule not found with ID: " + id);
        }
        callScheduleRepository.deleteById(id);
    }

    /**
     * Helper method to map CallSchedule entity to CallScheduleResponseDto.
     *
     * @param callSchedule The CallSchedule entity.
     * @return CallScheduleResponseDto.
     */
    private CallScheduleResponseDto mapToCallScheduleResponseDto(CallSchedule callSchedule) {
        CallScheduleResponseDto dto = new CallScheduleResponseDto();
        dto.setId(callSchedule.getId());
        dto.setKamId(callSchedule.getKam().getId());
        dto.setKamName(callSchedule.getKam().getName());
        dto.setLeadId(callSchedule.getLead().getId());
        dto.setLeadName(callSchedule.getLead().getName());
        dto.setLeadCity(callSchedule.getLead().getCity());
        dto.setScheduledDate(callSchedule.getScheduledDate());
        dto.setStatus(callSchedule.getStatus());
        dto.setPriority(callSchedule.getPriority());
        dto.setNextScheduledDate(callSchedule.getNextScheduledDate());
        return dto;
    }
}
