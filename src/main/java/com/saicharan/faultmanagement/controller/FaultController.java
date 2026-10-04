package com.saicharan.faultmanagement.controller;

import com.saicharan.faultmanagement.dto.FaultRequest;
import com.saicharan.faultmanagement.dto.FaultResponse;
import com.saicharan.faultmanagement.dto.FaultSummary;
import com.saicharan.faultmanagement.dto.StatusUpdateRequest;
import com.saicharan.faultmanagement.model.FaultStatus;
import com.saicharan.faultmanagement.model.Severity;
import com.saicharan.faultmanagement.service.FaultService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/faults")
public class FaultController {

    private final FaultService service;

    public FaultController(FaultService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<FaultResponse> create(@Valid @RequestBody FaultRequest request) {
        FaultResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<FaultResponse> list(@RequestParam(required = false) FaultStatus status,
                                    @RequestParam(required = false) Severity severity,
                                    @RequestParam(required = false) String device) {
        return service.search(status, severity, device);
    }

    @GetMapping("/{id}")
    public FaultResponse get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public FaultResponse update(@PathVariable Long id, @Valid @RequestBody FaultRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/status")
    public FaultResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return service.updateStatus(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/summary")
    public FaultSummary summary() {
        return service.summary();
    }
}
