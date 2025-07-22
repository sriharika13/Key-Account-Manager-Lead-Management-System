package com.kamleads.management.repository;

import com.kamleads.management.enums.CallStatus;
import com.kamleads.management.model.CallSchedule;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface CallScheduleRepository extends JpaRepository<CallSchedule, UUID> {

    // Calls scheduled for a specific KAM on a date
    @Query("SELECT cs FROM CallSchedule cs WHERE cs.kam.id = :kamId " +
            "AND cs.scheduledDate = :date " +
            "ORDER BY cs.priority ASC, cs.lead.name ASC")
    List<CallSchedule> findScheduledCallsForKamAndDate(@Param("kamId") UUID kamId,
                                                       @Param("date") LocalDate date);

    // Overdue pending calls
    @Query("SELECT cs FROM CallSchedule cs WHERE cs.kam.id = :kamId " +
            "AND cs.scheduledDate < :currentDate AND cs.status = 'PENDING' " +
            "ORDER BY cs.scheduledDate ASC")
    List<CallSchedule> findOverdueCallsForKam(@Param("kamId") UUID kamId,
                                              @Param("currentDate") LocalDate currentDate);

    // Calls by status
    List<CallSchedule> findByKamIdAndStatus(UUID kamId, CallStatus status);

    // Upcoming calls for a lead
    @Query("SELECT cs FROM CallSchedule cs WHERE cs.lead.id = :leadId " +
            "AND cs.scheduledDate >= :fromDate " +
            "ORDER BY cs.scheduledDate ASC")
    List<CallSchedule> findUpcomingCallsForLead(@Param("leadId") UUID leadId,
                                                @Param("fromDate") LocalDate fromDate);
}
