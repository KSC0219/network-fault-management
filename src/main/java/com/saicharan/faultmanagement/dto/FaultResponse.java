package com.saicharan.faultmanagement.dto;

import com.saicharan.faultmanagement.model.Fault;
import com.saicharan.faultmanagement.model.FaultStatus;
import com.saicharan.faultmanagement.model.Severity;

import java.time.LocalDateTime;

public record FaultResponse(
        Long id,
        String title,
        String description,
        String deviceName,
        String ipAddress,
        Severity severity,
        FaultStatus status,
        String assignedTo,
        String resolutionNotes,
        LocalDateTime reportedAt,
        LocalDateTime updatedAt,
        LocalDateTime resolvedAt
) {
    public static FaultResponse from(Fault f) {
        return new FaultResponse(
                f.getId(), f.getTitle(), f.getDescription(), f.getDeviceName(), f.getIpAddress(),
                f.getSeverity(), f.getStatus(), f.getAssignedTo(), f.getResolutionNotes(),
                f.getReportedAt(), f.getUpdatedAt(), f.getResolvedAt());
    }
}
