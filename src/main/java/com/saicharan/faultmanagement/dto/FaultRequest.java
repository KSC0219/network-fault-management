package com.saicharan.faultmanagement.dto;

import com.saicharan.faultmanagement.model.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Payload for creating or fully updating a fault. */
public record FaultRequest(
        @NotBlank(message = "title is required")
        @Size(max = 150)
        String title,

        @Size(max = 1000)
        String description,

        @NotBlank(message = "deviceName is required")
        @Size(max = 100)
        String deviceName,

        @Pattern(regexp = "^$|^((25[0-5]|2[0-4]\\d|1?\\d?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1?\\d?\\d)$",
                 message = "ipAddress must be a valid IPv4 address")
        String ipAddress,

        @NotNull(message = "severity is required (LOW, MEDIUM, HIGH, CRITICAL)")
        Severity severity,

        @Size(max = 100)
        String assignedTo
) {}
