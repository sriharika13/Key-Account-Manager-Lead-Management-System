// Custom Repository Interface
package com.kamleads.management.repository;

import com.kamleads.management.dto.LeadPerformanceDTO;
import com.kamleads.management.dto.LeadSummaryDto;
import com.kamleads.management.model.Lead;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LeadRepositoryCustom {
    List<Lead> findLeadsWithComplexFilters(UUID kamId, String city, Double minScore,
                                           LocalDate fromDate, LocalDate toDate);
    List<LeadPerformanceDTO> findLeadPerformanceMetrics(UUID kamId, int limit);
    List<Lead> findLeadsByPerformanceScore(UUID kamId, Double minScore);

    List<LeadPerformanceDTO> findLeadPerformanceAnalytics(UUID kamId, LocalDate startDate, LocalDate endDate);

    LeadSummaryDto getLeadSummaryForKam(UUID kamId);

    List<Object[]> countLeadsByStatus(UUID kamId);
}


//public interface LeadRepositoryCustom {
//    List<LeadPerformanceDto> findLeadPerformanceAnalytics(UUID kamId, LocalDate startDate, LocalDate endDate);
//    LeadSummaryDto getLeadSummaryForKam(UUID kamId);
//    Page<Lead> findLeadsWithFilters(UUID kamId, String searchTerm, List<LeadStatus> statuses, String city, Pageable pageable);
//}
