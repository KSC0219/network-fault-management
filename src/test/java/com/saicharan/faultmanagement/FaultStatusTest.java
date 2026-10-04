package com.saicharan.faultmanagement;

import com.saicharan.faultmanagement.model.FaultStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FaultStatusTest {

    @Test
    void allowedTransitions() {
        assertTrue(FaultStatus.OPEN.canMoveTo(FaultStatus.IN_PROGRESS));
        assertTrue(FaultStatus.IN_PROGRESS.canMoveTo(FaultStatus.RESOLVED));
        assertTrue(FaultStatus.RESOLVED.canMoveTo(FaultStatus.CLOSED));
        assertTrue(FaultStatus.RESOLVED.canMoveTo(FaultStatus.OPEN));
    }

    @Test
    void blockedTransitions() {
        assertFalse(FaultStatus.OPEN.canMoveTo(FaultStatus.CLOSED));
        assertFalse(FaultStatus.CLOSED.canMoveTo(FaultStatus.OPEN));
    }
}
