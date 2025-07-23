package com.kamleads.management.controller;

import com.kamleads.management.dto.LeadPerformanceDTO;
//import com.kamleads.management.dto.LeadPerformanceDto;
import com.kamleads.management.dto.LeadSummaryDto;
import com.kamleads.management.dto.request.LeadCreateRequestDto;
import com.kamleads.management.dto.response.LeadResponseDto;
import com.kamleads.management.enums.LeadStatus;
import com.kamleads.management.exception.ResourceNotFoundException;
import com.kamleads.management.service.LeadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadService leadService;

    @Autowired
    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    /**
     * Creates a new lead.
     * Accessible by 'KAM' role.
     */
    @PostMapping
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<LeadResponseDto> createLead(@Valid @RequestBody LeadCreateRequestDto requestDto) {
        LeadResponseDto createdLead = leadService.createLead(requestDto);
        return new ResponseEntity<>(createdLead, HttpStatus.CREATED);
    }

    /**
     * Retrieves a lead by ID.
     * Accessible by 'KAM' role.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<LeadResponseDto> getLeadById(@PathVariable UUID id) {
        LeadResponseDto lead = leadService.getLeadById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with ID: " + id));
        return ResponseEntity.ok(lead);
    }

    /**
     * Retrieves leads for a specific KAM with pagination and filters.
     * Accessible by 'KAM' role.
     *
     * @param kamId The ID of the KAM.
     * @param searchTerm Optional search term for lead name.
     * @param statuses Optional list of lead statuses to filter by.
     * @param city Optional city to filter by.
     * @param pageable Pagination information.
     */
    @GetMapping("/by-kam/{kamId}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<Page<LeadResponseDto>> getLeadsByKam(
            @PathVariable UUID kamId,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) List<LeadStatus> statuses,
            @RequestParam(required = false) String city,
            Pageable pageable) {
        Page<LeadResponseDto> leads = leadService.getLeadsByKam(kamId, searchTerm, statuses, city, pageable);
        return ResponseEntity.ok(leads);
    }

    /**
     * Updates an existing lead.
     * Accessible by 'KAM' role.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<LeadResponseDto> updateLead(@PathVariable UUID id, @Valid @RequestBody LeadCreateRequestDto requestDto) {
        LeadResponseDto updatedLead = leadService.updateLead(id, requestDto);
        return ResponseEntity.ok(updatedLead);
    }

    /**
     * Deletes a lead.
     * Accessible by 'KAM' role.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<Void> deleteLead(@PathVariable UUID id) {
        leadService.deleteLead(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates the status of a lead.
     * Accessible by 'KAM' role.
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<LeadResponseDto> updateLeadStatus(@PathVariable UUID id, @RequestParam LeadStatus newStatus) {
        LeadResponseDto updatedLead = leadService.updateLeadStatus(id, newStatus);
        return ResponseEntity.ok(updatedLead);
    }

    /**
     * Triggers calculation and update of a lead's performance score.
     * Accessible by 'KAM' role.
     */
    @PostMapping("/{id}/calculate-performance")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<LeadResponseDto> calculateAndUpdatePerformanceScore(@PathVariable UUID id) {
        LeadResponseDto updatedLead = leadService.calculateAndUpdatePerformanceScore(id);
        return ResponseEntity.ok(updatedLead);
    }

    /**
     * Retrieves lead performance analytics for a specific KAM within a date range.
     * Accessible by 'KAM' role.
     */
    @GetMapping("/analytics/performance/{kamId}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<List<LeadPerformanceDTO>> getLeadPerformanceAnalytics(
            @PathVariable UUID kamId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<LeadPerformanceDTO> analytics = leadService.getLeadPerformanceAnalytics(kamId, startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    /**
     * Retrieves a summary of leads for a specific KAM.
     * Accessible by 'KAM' role.
     */
    @GetMapping("/summary/{kamId}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<LeadSummaryDto> getLeadSummaryForKam(@PathVariable UUID kamId) {
        LeadSummaryDto summary = leadService.getLeadSummaryForKam(kamId);
        return ResponseEntity.ok(summary);
    }
}
