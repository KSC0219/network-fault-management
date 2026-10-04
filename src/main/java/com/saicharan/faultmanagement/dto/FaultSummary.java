package com.saicharan.faultmanagement.dto;

import java.util.Map;

/** Dashboard-style counts of faults by status and by severity. */
public record FaultSummary(
        long total,
        Map<String, Long> byStatus,
        Map<String, Long> activeBySeverity
) {}
