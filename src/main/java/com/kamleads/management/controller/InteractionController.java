package com.kamleads.management.controller;

import com.kamleads.management.dto.request.InteractionCreateRequestDto;
import com.kamleads.management.dto.response.InteractionResponseDto;
import com.kamleads.management.exception.ResourceNotFoundException;
import com.kamleads.management.service.InteractionService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/interactions")
public class InteractionController {

    private final InteractionService interactionService;

    @Autowired
    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    /**
     * Creates a new interaction.
     * Accessible by 'KAM' role.
     */
    @PostMapping
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<InteractionResponseDto> createInteraction(@Valid @RequestBody InteractionCreateRequestDto requestDto) {
        InteractionResponseDto createdInteraction = interactionService.createInteraction(requestDto);
        return new ResponseEntity<>(createdInteraction, HttpStatus.CREATED);
    }

    /**
     * Retrieves an interaction by ID.
     * Accessible by 'KAM' role.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<InteractionResponseDto> getInteractionById(@PathVariable UUID id) {
        InteractionResponseDto interaction = interactionService.getInteractionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interaction not found with ID: " + id));
        return ResponseEntity.ok(interaction);
    }

    /**
     * Retrieves all interactions for a specific lead with pagination.
     * Accessible by 'KAM' role.
     */
    @GetMapping("/by-lead/{leadId}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<Page<InteractionResponseDto>> getInteractionsByLeadId(@PathVariable UUID leadId, Pageable pageable) {
        Page<InteractionResponseDto> interactions = interactionService.getInteractionsByLeadId(leadId, pageable);
        return ResponseEntity.ok(interactions);
    }

    /**
     * Retrieves interactions for a specific KAM within a date range, with pagination.
     * Accessible by 'KAM' role.
     */
    @GetMapping("/by-kam/{kamId}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<Page<InteractionResponseDto>> getInteractionsByKamAndDateRange(
            @PathVariable UUID kamId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        Page<InteractionResponseDto> interactions = interactionService.getInteractionsByKamAndDateRange(kamId, startDate, endDate, pageable);
        return ResponseEntity.ok(interactions);
    }

    /**
     * Updates an existing interaction.
     * Accessible by 'KAM' role.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<InteractionResponseDto> updateInteraction(@PathVariable UUID id, @Valid @RequestBody InteractionCreateRequestDto requestDto) {
        InteractionResponseDto updatedInteraction = interactionService.updateInteraction(id, requestDto);
        return ResponseEntity.ok(updatedInteraction);
    }

    /**
     * Deletes an interaction.
     * Accessible by 'KAM' role.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<Void> deleteInteraction(@PathVariable UUID id) {
        interactionService.deleteInteraction(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a list of interactions requiring follow-up for a specific KAM.
     * Accessible by 'KAM' role.
     */
    @GetMapping("/follow-up/{kamId}")
    @PreAuthorize("hasRole('KAM')")
    public ResponseEntity<List<InteractionResponseDto>> getInteractionsRequiringFollowUp(
            @PathVariable UUID kamId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<InteractionResponseDto> interactions = interactionService.getInteractionsRequiringFollowUp(kamId, date);
        return ResponseEntity.ok(interactions);
    }
}
