package com.kamleads.management.repository;

import com.kamleads.management.dto.LeadPerformanceDTO;
import com.kamleads.management.dto.LeadSummaryDto;
import com.kamleads.management.enums.LeadStatus; // Added for findLeadsWithFilters
import com.kamleads.management.model.Lead;
import org.springframework.data.domain.Page; // Added for Page return type
import org.springframework.data.domain.Pageable; // Added for Pageable parameter
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Custom interface for LeadRepository to define methods that require custom
 * implementation (e.g., dynamic queries, complex business logic).
 */
public interface LeadRepositoryCustom {
    // Existing custom methods from your previous LeadRepositoryImpl
    List<Lead> findLeadsWithComplexFilters(UUID kamId, String city, Double minScore,
                                           LocalDate fromDate, LocalDate toDate);
    List<LeadPerformanceDTO> findLeadPerformanceMetrics(UUID kamId, int limit); // Corrected DTO name
    List<Lead> findLeadsByPerformanceScore(UUID kamId, Double minScore);

    // Methods from your commented-out LeadRepositoryImpl
    List<LeadPerformanceDTO> findLeadPerformanceAnalytics(UUID kamId, LocalDate startDate, LocalDate endDate);
    LeadSummaryDto getLeadSummaryForKam(UUID kamId);
    List<Object[]> countLeadsByStatus(UUID kamId);

    /**
     * Finds leads based on various filters (KAM ID, search term, statuses, city)
     * with pagination. This method requires a custom implementation due to its
     * dynamic nature.
     *
     * @param kamId The UUID of the Key Account Manager.
     * @param searchTerm A term to search within lead names (can be null for no search).
     * @param statuses A list of LeadStatus enums to filter by (can be null or empty for no status filter).
     * @param city The city to filter by (can be null for no city filter).
     * @param pageable Pagination and sorting information.
     * @return A Page of Lead entities matching the criteria.
     */
    Page<Lead> findLeadsWithFilters(UUID kamId, String searchTerm, List<LeadStatus> statuses, String city, Pageable pageable);
}
