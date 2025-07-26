package com.kamleads.management.repository.impl;

import com.kamleads.management.dto.LeadPerformanceDTO;
import com.kamleads.management.dto.LeadSummaryDto;
import com.kamleads.management.enums.LeadStatus;
import com.kamleads.management.model.Lead;
import com.kamleads.management.repository.LeadRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Hibernate 6 specific imports for Criteria API extensions
import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
// import org.hibernate.query.sqm.TemporalUnit; // Keep commented unless used in Criteria API directly for timestampadd

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

        if (city != null && !city.trim().isEmpty()) {
            jpql.append("AND l.city = :city ");
        }
        if (minScore != null) {
            jpql.append("AND l.performanceScore >= :minScore ");
        }
        if (fromDate != null && toDate != null) {
            // Using LocalDateTime for createdAt as it's a TIMESTAMP
            jpql.append("AND l.createdAt BETWEEN :fromDate AND :toDate ");
        }

        jpql.append("ORDER BY l.performanceScore DESC");

        TypedQuery<Lead> query = entityManager.createQuery(jpql.toString(), Lead.class);
        query.setParameter("kamId", kamId);

        if (city != null && !city.trim().isEmpty()) query.setParameter("city", city);
        if (minScore != null) query.setParameter("minScore", minScore);
        if (fromDate != null && toDate != null) {
            query.setParameter("fromDate", fromDate.atStartOfDay());
            query.setParameter("toDate", toDate.plusDays(1).atStartOfDay().minusNanos(1)); // To include end of toDate
        }

        return query.getResultList();
    }

    @Override
    public List<LeadPerformanceDTO> findLeadPerformanceMetrics(UUID kamId, int limit) {
        String jpql = """
            SELECT NEW com.kamleads.management.dto.LeadPerformanceDto(
                l.id, l.name, l.performanceScore,
                CAST(COUNT(i) AS long), COALESCE(SUM(i.orderValue), 0.0), COALESCE(AVG(i.orderValue), 0.0)
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

    @Override
    public List<LeadPerformanceDTO> findLeadPerformanceAnalytics(UUID kamId, LocalDate startDate, LocalDate endDate) {
        String jpql = """
            SELECT new com.kamleads.management.dto.LeadPerformanceDto(
                l.id, l.name, l.performanceScore,
                CAST(COUNT(i.id) AS long), COALESCE(SUM(i.orderValue), 0.0), COALESCE(AVG(i.orderValue), 0.0)
            )
            FROM Lead l
            LEFT JOIN l.interactions i ON i.interactionDate BETWEEN :startDate AND :endDate
            WHERE l.kam.id = :kamId
            GROUP BY l.id, l.name, l.performanceScore
            ORDER BY l.performanceScore DESC
            """;

        return entityManager.createQuery(jpql, LeadPerformanceDTO.class)
                .setParameter("kamId", kamId)
                .setParameter("startDate", startDate.atStartOfDay())
                .setParameter("endDate", endDate.plusDays(1).atStartOfDay().minusNanos(1)) // To include end of endDate
                .getResultList();
    }

    @Override
    public LeadSummaryDto getLeadSummaryForKam(UUID kamId) {
        // IMPORTANT: The FUNCTION('TIMESTAMPADD','DAY', ...) syntax with a String literal 'DAY'
        // is problematic in Hibernate 6's JPQL due to stricter type checking expecting TemporalUnit enum.
        // A direct JPQL fix is not straightforward as enums cannot be directly passed as string literals.
        //
        // Recommended solutions:
        // 1. Rewrite this specific part of the query using the Criteria API, where TemporalUnit enum can be used directly.
        //    (e.g., cb.function("timestampadd", LocalDateTime.class, cb.literal(TemporalUnit.DAY), ...))
        // 2. If you must keep JPQL, you might need to register a custom SQLFunction in your Hibernate configuration
        //    that correctly handles the 'TIMESTAMPADD' function with a string unit for your specific database.
        // 3. Re-evaluate if this complex date arithmetic can be done in Java after fetching relevant data,
        //    or simplified in the query if a database-agnostic HQL/JPQL alternative exists for your use case.

        String jpql = """
            SELECT new com.kamleads.management.dto.LeadSummaryDto(
                COUNT(l.id),
                COUNT(CASE WHEN l.status IN ('NEW', 'CONTACTED', 'INTERESTED', 'NEGOTIATING') THEN 1 END),
                COUNT(CASE WHEN l.lastCallDate IS NULL OR (FUNCTION('TIMESTAMPADD','DAY', l.callFrequency, l.lastCallDate)) <= CURRENT_DATE THEN 1 END),
                COALESCE(AVG(l.performanceScore), 0)
            )
            FROM Lead l
            WHERE l.kam.id = :kamId
            """;

        List<LeadSummaryDto> results = entityManager.createQuery(jpql, LeadSummaryDto.class)
                .setParameter("kamId", kamId)
                .getResultList();

        return results.isEmpty() ? new LeadSummaryDto(0L, 0L, 0L, BigDecimal.ZERO) : results.get(0);
    }

    @Override
    public List<Object[]> countLeadsByStatus(UUID kamId) {
        String jpql = """
            SELECT l.status, COUNT(l) FROM Lead l
            WHERE l.kam.id = :kamId
            GROUP BY l.status
            """;
        return entityManager.createQuery(jpql, Object[].class)
                .setParameter("kamId", kamId)
                .getResultList();
    }

    @Override
    public Page<Lead> findLeadsWithFilters(UUID kamId, String searchTerm, List<LeadStatus> statuses, String city, Pageable pageable) {
        // Unwrap EntityManager to get Hibernate's specific CriteriaBuilder
        HibernateCriteriaBuilder cb = (HibernateCriteriaBuilder) entityManager.unwrap(Session.class).getCriteriaBuilder();
        // Cast to JpaCriteriaQuery to access Hibernate-specific methods like createCountQuery()
        JpaCriteriaQuery<Lead> cq = (JpaCriteriaQuery<Lead>) cb.createQuery(Lead.class);
        Root<Lead> lead = cq.from(Lead.class);
        List<Predicate> predicates = new ArrayList<>();

        // Always filter by KAM ID
        predicates.add(cb.equal(lead.get("kam").get("id"), kamId));

        // Add search term filter if present
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(lead.get("name")), "%" + searchTerm.toLowerCase() + "%"));
        }

        // Add status filter if present
        if (statuses != null && !statuses.isEmpty()) {
            predicates.add(lead.get("status").in(statuses));
        }

        // Add city filter if present
        if (city != null && !city.trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(lead.get("city")), city.toLowerCase()));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        // Apply sorting from Pageable
        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                if (order.isAscending()) {
                    cq.orderBy(cb.asc(lead.get(order.getProperty())));
                } else {
                    cq.orderBy(cb.desc(lead.get(order.getProperty())));
                }
            });
        }

        TypedQuery<Lead> query = entityManager.createQuery(cq);

        // Apply pagination
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Lead> resultList = query.getResultList();

        // Fix for "Could not locate TableGroup": Use createCountQuery() on the main JpaCriteriaQuery
        // This method automatically re-uses the predicates from 'cq' for the count operation.
        JpaCriteriaQuery<Long> countQuery = cq.createCountQuery();
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(resultList, pageable, total);
    }
}