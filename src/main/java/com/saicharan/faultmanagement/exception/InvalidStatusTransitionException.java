package com.saicharan.faultmanagement.exception;

import com.saicharan.faultmanagement.model.FaultStatus;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(FaultStatus from, FaultStatus to) {
        super("Cannot change fault status from " + from + " to " + to);
    }
}
