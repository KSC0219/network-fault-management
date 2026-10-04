package com.saicharan.faultmanagement.dto;

import com.saicharan.faultmanagement.model.FaultStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Payload for moving a fault through its lifecycle. */
public record StatusUpdateRequest(
        @NotNull(message = "status is required")
        FaultStatus status,

        @Size(max = 1000)
        String resolutionNotes
) {}
