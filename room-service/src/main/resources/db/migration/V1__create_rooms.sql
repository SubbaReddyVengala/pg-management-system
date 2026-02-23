CREATE TABLE IF NOT EXISTS rooms (
    id                BIGSERIAL      PRIMARY KEY,
    room_number       VARCHAR(20)    NOT NULL UNIQUE,
    floor             INTEGER        NOT NULL,
    room_type         VARCHAR(20)    NOT NULL
CHECK (room_type IN ('SINGLE','DOUBLE','TRIPLE','DORMITORY')),
    max_capacity      INTEGER        NOT NULL DEFAULT 1,
    current_occupancy INTEGER        NOT NULL DEFAULT 0,
    monthly_rent      DECIMAL(10,2)  NOT NULL,
    status            VARCHAR(20)    NOT NULL DEFAULT 'AVAILABLE'
                      CHECK (status IN ('AVAILABLE','OCCUPIED','MAINTENANCE')),
    description       TEXT,
    created_at        TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_rooms_status ON rooms(status);
CREATE INDEX IF NOT EXISTS idx_rooms_floor  ON rooms(floor);
CREATE INDEX IF NOT EXISTS idx_rooms_type   ON rooms(room_type);
