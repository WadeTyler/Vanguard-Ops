CREATE TABLE IF NOT EXISTS maintenance_orders
(
    id           SERIAL PRIMARY KEY,                                                                                      -- Internal ID
    aircraft_id  INT         NOT NULL,                                                                                    -- Foreign key to aircraft
    status       VARCHAR(20) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')), -- Order status

    priority     INTEGER     NOT NULL DEFAULT 0 CHECK (priority >= 0),                                                    -- Priority level, higher number means higher priority

    description  TEXT        NOT NULL,

    placed_by    UUID,                                                                                                    -- User ID of the person who placed the order

    completed_by UUID,                                                                                                    -- User ID of the person who completed the order
    completed_at TIMESTAMP,                                                                                               -- Timestamp when the order was completed

    created_at   TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (aircraft_id) REFERENCES aircraft (id) ON DELETE CASCADE,
    FOREIGN KEY (placed_by) REFERENCES users (id),
    FOREIGN KEY (completed_by) REFERENCES users (id)
);

COMMIT;

-- aircraft_id index for faster lookups
CREATE INDEX idx_maintenance_orders_aircraft_id ON maintenance_orders (aircraft_id);