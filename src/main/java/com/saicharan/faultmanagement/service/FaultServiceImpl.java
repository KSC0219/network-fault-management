package com.saicharan.faultmanagement.service;

import com.saicharan.faultmanagement.dto.FaultRequest;
import com.saicharan.faultmanagement.dto.FaultResponse;
import com.saicharan.faultmanagement.dto.FaultSummary;
import com.saicharan.faultmanagement.dto.StatusUpdateRequest;
import com.saicharan.faultmanagement.exception.FaultNotFoundException;
import com.saicharan.faultmanagement.exception.InvalidStatusTransitionException;
import com.saicharan.faultmanagement.model.Fault;
import com.saicharan.faultmanagement.model.FaultStatus;
import com.saicharan.faultmanagement.model.Severity;
import com.saicharan.faultmanagement.repository.FaultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class FaultServiceImpl implements FaultService {

    private final FaultRepository repository;

    public FaultServiceImpl(FaultRepository repository) {
        this.repository = repository;
    }

    @Override
    public FaultResponse create(FaultRequest request) {
        Fault fault = new Fault();
        apply(fault, request);
        fault.setStatus(FaultStatus.OPEN);
        return FaultResponse.from(repository.save(fault));
    }

    @Override
    @Transactional(readOnly = true)
    public FaultResponse getById(Long id) {
        return FaultResponse.from(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FaultResponse> search(FaultStatus status, Severity severity, String deviceName) {
        List<Fault> faults;
        if (deviceName != null && !deviceName.isBlank()) {
            faults = repository.findByDeviceNameIgnoreCase(deviceName.trim());
        } else if (status != null && severity != null) {
            faults = repository.findByStatusAndSeverity(status, severity);
        } else if (status != null) {
            faults = repository.findByStatus(status);
        } else if (severity != null) {
            faults = repository.findBySeverity(severity);
        } else {
            faults = repository.findAll();
        }
        // device filter can still be combined with status/severity
        return faults.stream()
                .filter(f -> status == null || f.getStatus() == status)
                .filter(f -> severity == null || f.getSeverity() == severity)
                .map(FaultResponse::from)
                .toList();
    }

    @Override
    public FaultResponse update(Long id, FaultRequest request) {
        Fault fault = find(id);
        apply(fault, request);
        return FaultResponse.from(repository.save(fault));
    }

    @Override
    public FaultResponse updateStatus(Long id, StatusUpdateRequest request) {
        Fault fault = find(id);
        FaultStatus current = fault.getStatus();
        FaultStatus next = request.status();

        if (current == next) {
            return FaultResponse.from(fault);
        }
        if (!current.canMoveTo(next)) {
            throw new InvalidStatusTransitionException(current, next);
        }

        fault.setStatus(next);
        if (next == FaultStatus.RESOLVED) {
            fault.setResolvedAt(LocalDateTime.now());
        } else if (next == FaultStatus.OPEN) {
            fault.setResolvedAt(null); // re-opened
        }
        if (request.resolutionNotes() != null) {
            fault.setResolutionNotes(request.resolutionNotes());
        }
        return FaultResponse.from(repository.save(fault));
    }

    @Override
    public void delete(Long id) {
        repository.delete(find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public FaultSummary summary() {
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (FaultStatus s : FaultStatus.values()) {
            byStatus.put(s.name(), repository.countByStatus(s));
        }
        Map<String, Long> bySeverity = new LinkedHashMap<>();
        for (Severity s : Severity.values()) {
            bySeverity.put(s.name(), 0L);
        }
        for (Object[] row : repository.countBySeverityForStatuses(
                List.of(FaultStatus.OPEN, FaultStatus.IN_PROGRESS))) {
            bySeverity.put(((Severity) row[0]).name(), (Long) row[1]);
        }
        return new FaultSummary(repository.count(), byStatus, bySeverity);
    }

    private Fault find(Long id) {
        return repository.findById(id).orElseThrow(() -> new FaultNotFoundException(id));
    }

    private void apply(Fault fault, FaultRequest r) {
        fault.setTitle(r.title().trim());
        fault.setDescription(r.description());
        fault.setDeviceName(r.deviceName().trim());
        fault.setIpAddress(r.ipAddress() == null || r.ipAddress().isBlank() ? null : r.ipAddress());
        fault.setSeverity(r.severity());
        fault.setAssignedTo(r.assignedTo());
    }
}
