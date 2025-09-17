CREATE TABLE IF NOT EXISTS aircraft
(
    id                 SERIAL PRIMARY KEY,                                      -- Internal ID
    tail_number        VARCHAR(20)  NOT NULL UNIQUE,                            -- Unique tail number
    model              VARCHAR(50)  NOT NULL,                                   -- Aircraft model
    variant            VARCHAR(50),
    manufacturer       VARCHAR(100) NOT NULL,                                   -- Manufacturer name
    serial_number      VARCHAR(100) UNIQUE,                                     -- Unique serial number

    total_flight_hours NUMERIC(10, 2)        DEFAULT 0,                         -- Total flight hours
    total_cycles       INT                   DEFAULT 0,                         -- Total flight cycles
    last_flight_date   DATE,                                                    -- Date of last flight

    status             VARCHAR(20)  NOT NULL DEFAULT 'AVAILABLE'
        CHECK (status IN ('AVAILABLE', 'IN_MAINTENANCE', 'AOG', 'IN_SERVICE')), -- Operational status

    squadron           VARCHAR(50),                                             -- Assigned squadron or unit
    base_location      VARCHAR(100),                                            -- Base location of the aircraft

    created_at         TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP             DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO aircraft (tail_number, model, variant, manufacturer, serial_number,
                      total_flight_hours, total_cycles, last_flight_date,
                      status, squadron, base_location)
VALUES ('165532', 'F/A-18E', 'Block II', 'Boeing', 'B12345',
        1250.5, 430, '2025-09-10', 'AVAILABLE', 'VFA-103', 'NAS Oceana'),

       ('165533', 'F/A-18F', 'Super Hornet', 'Boeing', 'B12346',
        980.0, 390, '2025-09-12', 'IN_MAINTENANCE', 'VFA-211', 'NAS Lemoore'),

       ('167890', 'P-8A Poseidon', 'Multi-mission', 'Boeing', 'P89012',
        2300.0, 780, '2025-09-11', 'AVAILABLE', 'VP-45', 'NAS Jacksonville'),

       ('168001', 'C-130J Super Hercules', 'Transport', 'Lockheed Martin', 'C130J01',
        3400.5, 1200, '2025-09-09', 'AOG', 'VR-59', 'NAS Jacksonville'),

       ('165540', 'E-2D Hawkeye', 'Airborne Early Warning', 'Northrop Grumman', 'E2D1001',
        1500.2, 560, '2025-09-08', 'IN_MAINTENANCE', 'VAW-120', 'NAS Norfolk'),

       ('165550', 'MH-60R Seahawk', 'Anti-Submarine', 'Sikorsky', 'MH60R002',
        750.0, 210, '2025-09-07', 'AVAILABLE', 'HSM-41', 'NAS North Island');