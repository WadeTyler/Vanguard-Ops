-- Maintenance rules are used to define automated maintenance tasks for the database.

CREATE TABLE IF NOT EXISTS maintenance_rules
(
    id                SERIAL PRIMARY KEY,                                                                   -- Internal ID
    name              VARCHAR(255) NOT NULL UNIQUE,                                                         -- Unique name for the maintenance rule
    description       TEXT,                                                                                 -- Description of the maintenance rule
    rule_type  VARCHAR(50)  NOT NULL CHECK (rule_type IN ('FLIGHT_HOURS', 'CYCLES', 'DAYS')), -- Type of requirement
    threshold_value INT          NOT NULL CHECK (threshold_value > 0),                                  -- Value for the requirement
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,                                                  -- Timestamp when the rule was created
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP                                                   -- Timestamp when the rule was last updated
);

-- Index for faster lookups on requirement_type and requirement_value
CREATE INDEX idx_maintenance_rules_requirement ON maintenance_rules (rule_type, threshold_value);

COMMIT;

CREATE TABLE IF NOT EXISTS active_maintenance_rules
(
    aircraft_id    INT NOT NULL,
    rule_id        INT NOT NULL,
    last_triggered TIMESTAMP,
    last_value     INT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (aircraft_id, rule_id),
    FOREIGN KEY (aircraft_id) REFERENCES aircraft (id) ON DELETE CASCADE,
    FOREIGN KEY (rule_id) REFERENCES maintenance_rules (id) ON DELETE CASCADE
);

-- Index for faster lookups on aircraft_id
CREATE INDEX idx_applied_maintenance_rules_aircraft_id ON active_maintenance_rules (aircraft_id);
-- Index for faster lookups on rule_id
CREATE INDEX idx_applied_maintenance_rules_rule_id ON active_maintenance_rules (rule_id);

COMMIT;