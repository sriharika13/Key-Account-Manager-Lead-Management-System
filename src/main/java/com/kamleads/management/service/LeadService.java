package com.kamleads.management.service;

import com.kamleads.management.dto.LeadPerformanceDTO;
import com.kamleads.management.dto.LeadSummaryDto;
import com.kamleads.management.dto.request.LeadCreateRequestDto;
import com.kamleads.management.dto.ContactSummaryDto;
import com.kamleads.management.dto.response.LeadResponseDto;
import com.kamleads.management.dto.RecentInteractionsSummaryDto;
import com.kamleads.management.enums.LeadStatus;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LeadService {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final InteractionRepository interactionRepository;

    @Autowired
    public LeadService(LeadRepository leadRepository, UserRepository userRepository,
                       ContactRepository contactRepository, InteractionRepository interactionRepository) {
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
        this.interactionRepository = interactionRepository;
    }

    /**
     * Creates a new lead (restaurant).
     *
     * @param requestDto The DTO containing lead creation details.
     * @return LeadResponseDto of the created lead.
     * @throws RuntimeException if KAM not found.
     */
    @Transactional
    public LeadResponseDto createLead(LeadCreateRequestDto requestDto) {
        User kam = userRepository.findById(requestDto.getKamId())
                .orElseThrow(() -> new RuntimeException("KAM not found with ID: " + requestDto.getKamId()));

        Lead lead = new Lead();
        lead.setId(UUID.randomUUID());
        lead.setName(requestDto.getName());
        lead.setCity(requestDto.getCity());
        lead.setCuisineType(requestDto.getCuisineType());
        lead.setStatus(requestDto.getStatus());
        lead.setKam(kam);
        lead.setCallFrequency(requestDto.getCallFrequency());
        lead.setPerformanceScore(BigDecimal.ZERO); // Initialize performance score

        Lead savedLead = leadRepository.save(lead);
        return mapToLeadResponseDto(savedLead);
    }

    /**
     * Retrieves a lead by its ID, including related contacts and recent activity summary.
     *
     * @param id The UUID of the lead.
     * @return Optional<LeadResponseDto> if found, empty otherwise.
     */
    @Transactional(readOnly = true)
    public Optional<LeadResponseDto> getLeadById(UUID id) {
        return leadRepository.findById(id).map(this::mapToLeadResponseDto);
    }

    /**
     * Retrieves all leads for a specific KAM with pagination and filtering.
     *
     * @param kamId The UUID of the KAM.
     * @param searchTerm Optional search term for lead name.
     * @param statuses Optional list of lead statuses to filter by.
     * @param city Optional city to filter by.
     * @param pageable Pagination information.
     * @return Page of LeadResponseDto.
     * @throws RuntimeException if KAM not found.
     */
    @Transactional(readOnly = true)
    public Page<LeadResponseDto> getLeadsByKam(UUID kamId, String searchTerm,
                                               List<LeadStatus> statuses, String city,
                                               Pageable pageable) {
        if (!userRepository.existsById(kamId)) {
            throw new RuntimeException("KAM not found with ID: " + kamId);
        }

        Page<Lead> leadsPage = leadRepository.findLeadsWithFilters(kamId, searchTerm, statuses, city, pageable);
        List<LeadResponseDto> dtoList = leadsPage.getContent().stream()
                .map(this::mapToLeadResponseDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, leadsPage.getTotalElements());
    }

    /**
     * Updates an existing lead's details.
     *
     * @param id The UUID of the lead to update.
     * @param requestDto The DTO containing updated lead details.
     * @return LeadResponseDto of the updated lead.
     * @throws RuntimeException if lead or KAM not found.
     */
    @Transactional
    public LeadResponseDto updateLead(UUID id, LeadCreateRequestDto requestDto) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found with ID: " + id));

        User kam = userRepository.findById(requestDto.getKamId())
                .orElseThrow(() -> new RuntimeException("KAM not found with ID: " + requestDto.getKamId()));

        lead.setName(requestDto.getName());
        lead.setCity(requestDto.getCity());
        lead.setCuisineType(requestDto.getCuisineType());
        lead.setStatus(requestDto.getStatus());
        lead.setKam(kam);
        lead.setCallFrequency(requestDto.getCallFrequency());

        Lead updatedLead = leadRepository.save(lead);
        return mapToLeadResponseDto(updatedLead);
    }

    /**
     * Deletes a lead by its ID.
     *
     * @param id The UUID of the lead to delete.
     * @throws RuntimeException if lead not found.
     */
    @Transactional
    public void deleteLead(UUID id) {
        if (!leadRepository.existsById(id)) {
            throw new RuntimeException("Lead not found with ID: " + id);
        }
        leadRepository.deleteById(id);
    }

    /**
     * Updates the status of a lead.
     *
     * @param leadId The UUID of the lead.
     * @param newStatus The new status for the lead.
     * @return LeadResponseDto of the updated lead.
     * @throws RuntimeException if lead not found.
     */
    @Transactional
    public LeadResponseDto updateLeadStatus(UUID leadId, LeadStatus newStatus) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found with ID: " + leadId));
        lead.setStatus(newStatus);
        Lead updatedLead = leadRepository.save(lead);
        return mapToLeadResponseDto(updatedLead);
    }

    /**
     * Calculates and updates the performance score for a specific lead.
     * This is a simplified example; actual calculation might involve more complex logic
     * based on interactions, order values, etc., over a period.
     *
     * @param leadId The UUID of the lead.
     * @return LeadResponseDto with updated performance score.
     * @throws RuntimeException if lead not found.
     */
    @Transactional
    public LeadResponseDto calculateAndUpdatePerformanceScore(UUID leadId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found with ID: " + leadId));

        // Example calculation: Performance score based on total order value in last 90 days
        // and number of interactions. This is a placeholder.
        LocalDateTime ninetyDaysAgo = LocalDateTime.now().minusDays(90);
        BigDecimal totalOrderValue = interactionRepository.calculateTotalOrderValueByLeadAndDateRange(
                leadId, ninetyDaysAgo, LocalDateTime.now());
        Long totalInteractions = interactionRepository.findInteractionCountByKamAndType(
                lead.getKam().getId(), InteractionType.CALL, LocalDateTime.now()); // Example: count calls

        // Simple scoring logic (adjust as needed)
        BigDecimal newPerformanceScore = BigDecimal.ZERO;
        if (totalOrderValue != null) {
            newPerformanceScore = newPerformanceScore.add(totalOrderValue.divide(BigDecimal.valueOf(1000), 2, BigDecimal.ROUND_HALF_UP));
        }
        if (totalInteractions != null) {
            newPerformanceScore = newPerformanceScore.add(BigDecimal.valueOf(totalInteractions).multiply(BigDecimal.valueOf(5)));
        }
        // Cap score at 100 for example
        newPerformanceScore = newPerformanceScore.min(BigDecimal.valueOf(100));

        lead.setPerformanceScore(newPerformanceScore);
        Lead updatedLead = leadRepository.save(lead);
        return mapToLeadResponseDto(updatedLead);
    }

    /**
     * Retrieves performance analytics for leads under a specific KAM within a date range.
     *
     * @param kamId The UUID of the KAM.
     * @param startDate Start date for analytics.
     * @param endDate End date for analytics.
     * @return List of LeadPerformanceDto.
     */
    @Transactional(readOnly = true)
    public List<LeadPerformanceDTO> getLeadPerformanceAnalytics(UUID kamId, LocalDate startDate, LocalDate endDate) {
        return leadRepository.findLeadPerformanceAnalytics(kamId, startDate, endDate);
    }

    /**
     * Retrieves a summary of leads for a specific KAM (total, active, requiring calls, average score).
     *
     * @param kamId The UUID of the KAM.
     * @return LeadSummaryDto.
     */
    @Transactional(readOnly = true)
    public LeadSummaryDto getLeadSummaryForKam(UUID kamId) {
        return leadRepository.getLeadSummaryForKam(kamId);
    }

    /**
     * Helper method to map Lead entity to LeadResponseDto.
     * Populates contacts and recent activity summary.
     *
     * @param lead The Lead entity.
     * @return LeadResponseDto.
     */
    private LeadResponseDto mapToLeadResponseDto(Lead lead) {
        LeadResponseDto dto = new LeadResponseDto();
        dto.setId(lead.getId());
        dto.setName(lead.getName());
        dto.setCity(lead.getCity());
        dto.setCuisineType(lead.getCuisineType());
        dto.setStatus(lead.getStatus());
        dto.setKamId(lead.getKam().getId());
        dto.setKamName(lead.getKam().getName()); // Assuming KAM name is accessible
        dto.setCallFrequency(lead.getCallFrequency());
        dto.setLastCallDate(lead.getLastCallDate());
        dto.setPerformanceScore(lead.getPerformanceScore());

        // Calculate next call date
        if (lead.getLastCallDate() != null) {
            dto.setNextCallDate(lead.getLastCallDate().plusDays(lead.getCallFrequency()));
        } else {
            // If no last call date, next call is today (or based on some initial rule)
            dto.setNextCallDate(LocalDate.now());
        }

        // Determine if call is required today
        dto.setRequiresCallToday(
                (lead.getLastCallDate() == null ||
                        lead.getLastCallDate().plusDays(lead.getCallFrequency()).isBefore(LocalDate.now()) ||
                        lead.getLastCallDate().plusDays(lead.getCallFrequency()).isEqual(LocalDate.now())) &&
                        !List.of(LeadStatus.CLOSED_WON, LeadStatus.CLOSED_LOST).contains(lead.getStatus())
        );

        // Populate contacts summary
        List<Contact> contacts = contactRepository.findByLeadIdOrderByNameAsc(lead.getId());
        dto.setTotalContacts(contacts.size());
        dto.setContacts(contacts.stream()
                .map(this::mapToContactSummaryDto)
                .collect(Collectors.toList()));

        // Populate recent activity summary
        dto.setRecentActivity(getRecentInteractionsSummary(lead.getId()));

        return dto;
    }

    /**
     * Helper method to map Contact entity to ContactSummaryDto.
     *
     * @param contact The Contact entity.
     * @return ContactSummaryDto.
     */
    private ContactSummaryDto mapToContactSummaryDto(Contact contact) {
        ContactSummaryDto dto = new ContactSummaryDto();
        dto.setId(contact.getId());
        dto.setName(contact.getName());
        dto.setRole(contact.getRole());
        dto.setEmail(contact.getEmail());
        return dto;
    }

    /**
     * Helper method to get recent interactions summary for a lead.
     *
     * @param leadId The UUID of the lead.
     * @return RecentInteractionsSummaryDto.
     */
    private RecentInteractionsSummaryDto getRecentInteractionsSummary(UUID leadId) {
        List<Interaction> recentInteractions = interactionRepository.findRecentInteractionsByLeadId(leadId, Pageable.ofSize(1));
        RecentInteractionsSummaryDto summaryDto = new RecentInteractionsSummaryDto();

        if (!recentInteractions.isEmpty()) {
            Interaction latestInteraction = recentInteractions.get(0);
            summaryDto.setLatestInteractionId(latestInteraction.getId());
            summaryDto.setLatestInteractionType(latestInteraction.getType().name());
            summaryDto.setLatestInteractionDate(latestInteraction.getInteractionDate());
        }

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Long totalInteractionsLast30Days = interactionRepository.countByLeadIdAndInteractionDateAfter(leadId, thirtyDaysAgo);
        Long totalOrdersLast30Days = interactionRepository.countByLeadIdAndTypeAndInteractionDateAfter(leadId, InteractionType.ORDER, thirtyDaysAgo);
        BigDecimal totalOrderValueLast30Days = interactionRepository.calculateTotalOrderValueByLeadAndDateRange(leadId, thirtyDaysAgo, LocalDateTime.now());

        summaryDto.setTotalInteractionsLast30Days(totalInteractionsLast30Days != null ? totalInteractionsLast30Days : 0L);
        summaryDto.setTotalOrdersLast30Days(totalOrdersLast30Days != null ? totalOrdersLast30Days : 0L);
        summaryDto.setTotalOrderValueLast30Days(totalOrderValueLast30Days != null ? totalOrderValueLast30Days : BigDecimal.ZERO);

        return summaryDto;
    }
}
