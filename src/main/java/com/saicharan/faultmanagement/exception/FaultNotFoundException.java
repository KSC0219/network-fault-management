package com.saicharan.faultmanagement.exception;

public class FaultNotFoundException extends RuntimeException {
    public FaultNotFoundException(Long id) {
        super("Fault not found with id " + id);
    }
}
