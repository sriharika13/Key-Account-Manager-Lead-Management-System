// Custom Repository Implementation
package com.kamleads.management.repository.impl;

import com.kamleads.management.dto.LeadPerformanceDTO;
import com.kamleads.management.model.Lead;
import com.kamleads.management.repository.LeadRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class LeadRepositoryImpl implements LeadRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Lead> findLeadsWithComplexFilters(UUID kamId, String city, Double minScore,
                                                  LocalDate fromDate, LocalDate toDate) {
        StringBuilder jpql = new StringBuilder(
                "SELECT l FROM Lead l WHERE l.kam.id = :kamId "
        );

        if (city != null) {
            jpql.append("AND l.city = :city ");
        }
        if (minScore != null) {
            jpql.append("AND l.performanceScore >= :minScore ");
        }
        if (fromDate != null && toDate != null) {
            jpql.append("AND l.createdAt BETWEEN :fromDate AND :toDate ");
        }

        jpql.append("ORDER BY l.performanceScore DESC");

        TypedQuery<Lead> query = entityManager.createQuery(jpql.toString(), Lead.class);
        query.setParameter("kamId", kamId);

        if (city != null) query.setParameter("city", city);
        if (minScore != null) query.setParameter("minScore", minScore);
        if (fromDate != null) {
            query.setParameter("fromDate", fromDate.atStartOfDay());
            query.setParameter("toDate", toDate.plusDays(1).atStartOfDay());
        }

        return query.getResultList();
    }

    @Override
    public List<LeadPerformanceDTO> findLeadPerformanceMetrics(UUID kamId, int limit) {
        String jpql = """
            SELECT NEW com.kamleads.management.dto.LeadPerformanceDTO(
                l.id, l.name, l.performanceScore, 
                COUNT(i), COALESCE(SUM(i.orderValue), 0)
            )
            FROM Lead l 
            LEFT JOIN l.interactions i 
            WHERE l.kam.id = :kamId 
            AND l.status NOT IN ('CLOSED_LOST', 'INACTIVE')
            GROUP BY l.id, l.name, l.performanceScore 
            ORDER BY l.performanceScore DESC
        """;

        TypedQuery<LeadPerformanceDTO> query = entityManager.createQuery(jpql, LeadPerformanceDTO.class);
        query.setParameter("kamId", kamId);
        query.setMaxResults(limit);

        return query.getResultList();
    }

    @Override
    public List<Lead> findLeadsByPerformanceScore(UUID kamId, Double minScore) {
        String jpql = """
            SELECT l FROM Lead l 
            WHERE l.kam.id = :kamId 
            AND l.performanceScore >= :minScore
            AND l.status IN ('INTERESTED', 'NEGOTIATING')
            ORDER BY l.performanceScore DESC, l.lastCallDate ASC
        """;

        TypedQuery<Lead> query = entityManager.createQuery(jpql, Lead.class);
        query.setParameter("kamId", kamId);
        query.setParameter("minScore", minScore);

        return query.getResultList();
    }
}


//
//@Repository
//public class LeadRepositoryImpl implements LeadRepositoryCustom {
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Override
//    public List<LeadPerformanceDto> findLeadPerformanceAnalytics(UUID kamId, LocalDate startDate, LocalDate endDate) {
//        String jpql = """
//            SELECT new com.kamleads.management.dto.LeadPerformanceDto(
//                l.id, l.name, l.performanceScore,
//                COUNT(i.id), COALESCE(SUM(i.orderValue), 0), COALESCE(AVG(i.orderValue), 0)
//            )
//            FROM Lead l
//            LEFT JOIN l.interactions i ON i.interactionDate BETWEEN :startDate AND :endDate
//            WHERE l.kam.id = :kamId
//            GROUP BY l.id, l.name, l.performanceScore
//            ORDER BY l.performanceScore DESC
//            """;
//
//        return entityManager.createQuery(jpql, LeadPerformanceDto.class)
//                .setParameter("kamId", kamId)
//                .setParameter("startDate", startDate.atStartOfDay())
//                .setParameter("endDate", endDate.plusDays(1).atStartOfDay())
//                .getResultList();
//    }
//
//    @Override
//    public LeadSummaryDto getLeadSummaryForKam(UUID kamId) {
//        String jpql = """
//            SELECT new com.kamleads.management.dto.LeadSummaryDto(
//                COUNT(l.id),
//                COUNT(CASE WHEN l.status IN ('NEW', 'CONTACTED', 'INTERESTED', 'NEGOTIATING') THEN 1 END),
//                COUNT(CASE WHEN l.lastCallDate IS NULL OR (l.lastCallDate + INTERVAL l.callFrequency DAY) <= CURRENT_DATE THEN 1 END),
//                COALESCE(AVG(l.performanceScore), 0)
//            )
//            FROM Lead l
//            WHERE l.kam.id = :kamId
//            """;
//
//        List<LeadSummaryDto> results = entityManager.createQuery(jpql, LeadSummaryDto.class)
//                .setParameter("kamId", kamId)
//                .getResultList();
//
//        return results.isEmpty() ? new LeadSummaryDto(0L, 0L, 0L, BigDecimal.ZERO) : results.get(0);
//    }
//
//    @Override
//    public Page<Lead> findLeadsWithFilters(UUID kamId, String searchTerm, List<LeadStatus> statuses, String city, Pageable pageable) {
//        StringBuilder jpql = new StringBuilder("SELECT l FROM Lead l WHERE l.kam.id = :kamId");
//
//        if (searchTerm != null && !searchTerm.isBlank())
//            jpql.append(" AND LOWER(l.name) LIKE LOWER(:searchTerm)");
//
//        if (statuses != null && !statuses.isEmpty())
//            jpql.append(" AND l.status IN :statuses");
//
//        if (city != null && !city.isBlank())
//            jpql.append(" AND LOWER(l.city) = LOWER(:city)");
//
//        jpql.append(" ORDER BY l.performanceScore DESC, l.name ASC");
//
//        TypedQuery<Lead> query = entityManager.createQuery(jpql.toString(), Lead.class);
//        query.setParameter("kamId", kamId);
//        if (searchTerm != null && !searchTerm.isBlank())
//            query.setParameter("searchTerm", "%" + searchTerm.trim() + "%");
//        if (statuses != null && !statuses.isEmpty())
//            query.setParameter("statuses", statuses);
//        if (city != null && !city.isBlank())
//            query.setParameter("city", city);
//
//        query.setFirstResult((int) pageable.getOffset());
//        query.setMaxResults(pageable.getPageSize());
//        List<Lead> leads = query.getResultList();
//
//        String countJpql = jpql.toString().replaceFirst("SELECT l", "SELECT COUNT(l)");
//        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
//        countQuery.setParameter("kamId", kamId);
//        if (searchTerm != null && !searchTerm.isBlank())
//            countQuery.setParameter("searchTerm", "%" + searchTerm.trim() + "%");
//        if (statuses != null && !statuses.isEmpty())
//            countQuery.setParameter("statuses", statuses);
//        if (city != null && !city.isBlank())
//            countQuery.setParameter("city", city);
//
//        long total = countQuery.getSingleResult();
//        return new PageImpl<>(leads, pageable, total);
//    }
//}
