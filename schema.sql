-- Reference MySQL schema (Hibernate creates this automatically with ddl-auto=update)
CREATE DATABASE IF NOT EXISTS fault_db;
USE fault_db;

CREATE TABLE IF NOT EXISTS faults (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(150)  NOT NULL,
    description      VARCHAR(1000),
    device_name      VARCHAR(100)  NOT NULL,
    ip_address       VARCHAR(45),
    severity         VARCHAR(20)   NOT NULL,
    status           VARCHAR(20)   NOT NULL,
    assigned_to      VARCHAR(100),
    resolution_notes VARCHAR(1000),
    reported_at      DATETIME(6)   NOT NULL,
    updated_at       DATETIME(6),
    resolved_at      DATETIME(6)
);
