package com.saicharan.faultmanagement.service;

import com.saicharan.faultmanagement.dto.FaultRequest;
import com.saicharan.faultmanagement.dto.FaultResponse;
import com.saicharan.faultmanagement.dto.FaultSummary;
import com.saicharan.faultmanagement.dto.StatusUpdateRequest;
import com.saicharan.faultmanagement.model.FaultStatus;
import com.saicharan.faultmanagement.model.Severity;

import java.util.List;

public interface FaultService {

    FaultResponse create(FaultRequest request);

    FaultResponse getById(Long id);

    List<FaultResponse> search(FaultStatus status, Severity severity, String deviceName);

    FaultResponse update(Long id, FaultRequest request);

    FaultResponse updateStatus(Long id, StatusUpdateRequest request);

    void delete(Long id);

    FaultSummary summary();
}
