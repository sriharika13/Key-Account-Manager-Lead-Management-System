package com.kamleads.management.service;

import com.kamleads.management.dto.request.InteractionCreateRequestDto;
import com.kamleads.management.dto.response.InteractionResponseDto;
import com.kamleads.management.enums.InteractionStatus;
import com.kamleads.management.enums.InteractionType;
import com.kamleads.management.model.Contact;
import com.kamleads.management.model.Interaction;
import com.kamleads.management.model.Lead;
import com.kamleads.management.model.User;
import com.kamleads.management.repository.ContactRepository;
import com.kamleads.management.repository.InteractionRepository;
import com.kamleads.management.repository.LeadRepository;
import com.kamleads.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InteractionService {

    private final InteractionRepository interactionRepository;
    private final LeadRepository leadRepository;
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    @Autowired
    public InteractionService(InteractionRepository interactionRepository, LeadRepository leadRepository,
                              ContactRepository contactRepository, UserRepository userRepository) {
        this.interactionRepository = interactionRepository;
        this.leadRepository = leadRepository;
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new interaction (call, order, email, meeting).
     * Automatically sets interaction date to now if not provided implicitly by the DTO.
     * Updates lead's lastCallDate if the interaction is a 'CALL' and 'COMPLETED'.
     *
     * @param requestDto The DTO containing interaction creation details.
     * @return InteractionResponseDto of the created interaction.
     * @throws RuntimeException if lead or KAM not found, or contact not found if provided.
     */
    @Transactional
    public InteractionResponseDto createInteraction(InteractionCreateRequestDto requestDto) {
        Lead lead = leadRepository.findById(requestDto.getLeadId())
                .orElseThrow(() -> new RuntimeException("Lead not found with ID: " + requestDto.getLeadId()));

        User kam = userRepository.findById(requestDto.getKamId())
                .orElseThrow(() -> new RuntimeException("KAM not found with ID: " + requestDto.getKamId()));

        Contact contact = null;
        if (requestDto.getContactId() != null) {
            contact = contactRepository.findById(requestDto.getContactId())
                    .orElseThrow(() -> new RuntimeException("Contact not found with ID: " + requestDto.getContactId()));
            // Ensure contact belongs to the specified lead
            if (!contact.getLead().getId().equals(lead.getId())) {
                throw new IllegalArgumentException("Contact does not belong to the specified lead.");
            }
        }

        Interaction interaction = new Interaction();
        interaction.setId(UUID.randomUUID());
        interaction.setLead(lead);
        interaction.setContact(contact);
        interaction.setKam(kam);
        interaction.setType(requestDto.getType());
        interaction.setStatus(requestDto.getStatus());
        interaction.setInteractionDate(LocalDateTime.now()); // Set current timestamp for interaction
        interaction.setOrderValue(requestDto.getOrderValue());
        interaction.setFollowUpDate(requestDto.getFollowUpDate());

        Interaction savedInteraction = interactionRepository.save(interaction);

        // Update lead's last call date if this is a completed call
        if (InteractionType.CALL.equals(savedInteraction.getType()) && InteractionStatus.COMPLETED.equals(savedInteraction.getStatus())) {
            lead.setLastCallDate(savedInteraction.getInteractionDate().toLocalDate());
            leadRepository.save(lead);
        }

        return mapToInteractionResponseDto(savedInteraction);
    }

    /**
     * Retrieves an interaction by its ID.
     *
     * @param id The UUID of the interaction.
     * @return Optional<InteractionResponseDto> if found, empty otherwise.
     */
    @Transactional(readOnly = true)
    public Optional<InteractionResponseDto> getInteractionById(UUID id) {
        return interactionRepository.findById(id).map(this::mapToInteractionResponseDto);
    }

    /**
     * Retrieves all interactions for a specific lead, with pagination for recent interactions.
     *
     * @param leadId The UUID of the lead.
     * @param pageable Pagination information (e.g., for recent interactions).
     * @return Page of InteractionResponseDto.
     * @throws RuntimeException if lead not found.
     */
    @Transactional(readOnly = true)
    public Page<InteractionResponseDto> getInteractionsByLeadId(UUID leadId, Pageable pageable) {
        if (!leadRepository.existsById(leadId)) {
            throw new RuntimeException("Lead not found with ID: " + leadId);
        }
        Page<Interaction> interactionsPage = interactionRepository.findByLeadId(leadId, pageable); // Assuming findByLeadId exists
        List<InteractionResponseDto> dtoList = interactionsPage.getContent().stream()
                .map(this::mapToInteractionResponseDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, interactionsPage.getTotalElements());
    }

    /**
     * Retrieves interactions for a specific KAM within a date range, with pagination.
     *
     * @param kamId The UUID of the KAM.
     * @param startDate Start date for interactions.
     * @param endDate End date for interactions.
     * @param pageable Pagination information.
     * @return Page of InteractionResponseDto.
     * @throws RuntimeException if KAM not found.
     */
    @Transactional(readOnly = true)
    public Page<InteractionResponseDto> getInteractionsByKamAndDateRange(UUID kamId,
                                                                         LocalDateTime startDate,
                                                                         LocalDateTime endDate,
                                                                         Pageable pageable) {
        if (!userRepository.existsById(kamId)) {
            throw new RuntimeException("KAM not found with ID: " + kamId);
        }
        Page<Interaction> interactionsPage = interactionRepository.findByKamIdAndDateRange(kamId, startDate, endDate, pageable);
        List<InteractionResponseDto> dtoList = interactionsPage.getContent().stream()
                .map(this::mapToInteractionResponseDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, interactionsPage.getTotalElements());
    }

    /**
     * Updates an existing interaction's details.
     *
     * @param id The UUID of the interaction to update.
     * @param requestDto The DTO containing updated interaction details.
     * @return InteractionResponseDto of the updated interaction.
     * @throws RuntimeException if interaction, lead, KAM, or contact not found, or contact-lead mismatch.
     */
    @Transactional
    public InteractionResponseDto updateInteraction(UUID id, InteractionCreateRequestDto requestDto) {
        Interaction interaction = interactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interaction not found with ID: " + id));

        Lead lead = leadRepository.findById(requestDto.getLeadId())
                .orElseThrow(() -> new RuntimeException("Lead not found with ID: " + requestDto.getLeadId()));

        User kam = userRepository.findById(requestDto.getKamId())
                .orElseThrow(() -> new RuntimeException("KAM not found with ID: " + requestDto.getKamId()));

        Contact contact = null;
        if (requestDto.getContactId() != null) {
            contact = contactRepository.findById(requestDto.getContactId())
                    .orElseThrow(() -> new RuntimeException("Contact not found with ID: " + requestDto.getContactId()));
            if (!contact.getLead().getId().equals(lead.getId())) {
                throw new IllegalArgumentException("Contact does not belong to the specified lead.");
            }
        }

        // Prevent changing lead/KAM for an existing interaction (usually not allowed)
        if (!interaction.getLead().getId().equals(lead.getId())) {
            throw new IllegalArgumentException("Cannot change lead for an existing interaction.");
        }
        if (!interaction.getKam().getId().equals(kam.getId())) {
            throw new IllegalArgumentException("Cannot change KAM for an existing interaction.");
        }

        interaction.setContact(contact);
        interaction.setType(requestDto.getType());
        interaction.setStatus(requestDto.getStatus());
        // Do not update interactionDate automatically on update, only on creation
        // interaction.setInteractionDate(LocalDateTime.now());
        interaction.setOrderValue(requestDto.getOrderValue());
        interaction.setFollowUpDate(requestDto.getFollowUpDate());

        Interaction updatedInteraction = interactionRepository.save(interaction);

        // Update lead's last call date if this is a completed call
        if (InteractionType.CALL.equals(updatedInteraction.getType()) && InteractionStatus.COMPLETED.equals(updatedInteraction.getStatus())) {
            lead.setLastCallDate(updatedInteraction.getInteractionDate().toLocalDate());
            leadRepository.save(lead);
        }

        return mapToInteractionResponseDto(updatedInteraction);
    }

    /**
     * Deletes an interaction by its ID.
     *
     * @param id The UUID of the interaction to delete.
     * @throws RuntimeException if interaction not found.
     */
    @Transactional
    public void deleteInteraction(UUID id) {
        if (!interactionRepository.existsById(id)) {
            throw new RuntimeException("Interaction not found with ID: " + id);
        }
        interactionRepository.deleteById(id);
    }

    /**
     * Retrieves a list of interactions requiring follow-up for a specific KAM.
     *
     * @param kamId The UUID of the KAM.
     * @param date The date to check for follow-ups (e.g., today).
     * @return List of InteractionResponseDto.
     */
    @Transactional(readOnly = true)
    public List<InteractionResponseDto> getInteractionsRequiringFollowUp(UUID kamId, LocalDate date) {
        return interactionRepository.findInteractionsRequiringFollowUp(kamId, date).stream()
                .map(this::mapToInteractionResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to map Interaction entity to InteractionResponseDto.
     *
     * @param interaction The Interaction entity.
     * @return InteractionResponseDto.
     */
    private InteractionResponseDto mapToInteractionResponseDto(Interaction interaction) {
        InteractionResponseDto dto = new InteractionResponseDto();
        dto.setId(interaction.getId());
        dto.setLeadId(interaction.getLead().getId());
        dto.setLeadName(interaction.getLead().getName());
        dto.setKamId(interaction.getKam().getId());
        dto.setKamName(interaction.getKam().getName());
        dto.setType(interaction.getType());
        dto.setStatus(interaction.getStatus());
        dto.setInteractionDate(interaction.getInteractionDate());
        dto.setOrderValue(interaction.getOrderValue());
        dto.setFollowUpDate(interaction.getFollowUpDate());

        if (interaction.getContact() != null) {
            dto.setContactId(interaction.getContact().getId());
            dto.setContactName(interaction.getContact().getName());
        }
        return dto;
    }
}
