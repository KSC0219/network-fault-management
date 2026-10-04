package com.saicharan.faultmanagement.repository;

import com.saicharan.faultmanagement.model.Fault;
import com.saicharan.faultmanagement.model.FaultStatus;
import com.saicharan.faultmanagement.model.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface FaultRepository extends JpaRepository<Fault, Long> {

    List<Fault> findByStatus(FaultStatus status);

    List<Fault> findBySeverity(Severity severity);

    List<Fault> findByStatusAndSeverity(FaultStatus status, Severity severity);

    List<Fault> findByDeviceNameIgnoreCase(String deviceName);

    long countByStatus(FaultStatus status);

    /** Returns rows of [Severity, Long count] for faults in any of the given statuses. */
    @Query("SELECT f.severity, COUNT(f) FROM Fault f WHERE f.status IN :statuses GROUP BY f.severity")
    List<Object[]> countBySeverityForStatuses(@Param("statuses") Collection<FaultStatus> statuses);
}
