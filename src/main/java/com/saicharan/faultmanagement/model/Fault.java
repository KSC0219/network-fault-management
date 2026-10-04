package com.saicharan.faultmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "faults")
public class Fault {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 1000)
    private String description;

    /** Network device where the fault was detected, e.g. "router-blr-01". */
    @Column(nullable = false, length = 100)
    private String deviceName;

    @Column(length = 45)
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FaultStatus status = FaultStatus.OPEN;

    @Column(length = 100)
    private String assignedTo;

    @Column(length = 1000)
    private String resolutionNotes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime reportedAt;

    private LocalDateTime updatedAt;

    private LocalDateTime resolvedAt;

    @PrePersist
    void onCreate() {
        reportedAt = LocalDateTime.now();
        updatedAt = reportedAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public FaultStatus getStatus() { return status; }
    public void setStatus(FaultStatus status) { this.status = status; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public LocalDateTime getReportedAt() { return reportedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}
