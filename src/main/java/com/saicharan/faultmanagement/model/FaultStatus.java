package com.saicharan.faultmanagement.model;

/**
 * Lifecycle of a fault: OPEN -> IN_PROGRESS -> RESOLVED -> CLOSED.
 * A RESOLVED fault may be re-opened if the issue comes back.
 */
public enum FaultStatus {
    OPEN, IN_PROGRESS, RESOLVED, CLOSED;

    public boolean canMoveTo(FaultStatus next) {
        return switch (this) {
            case OPEN -> next == IN_PROGRESS || next == RESOLVED;
            case IN_PROGRESS -> next == RESOLVED || next == OPEN;
            case RESOLVED -> next == CLOSED || next == OPEN;
            case CLOSED -> false;
        };
    }
}
