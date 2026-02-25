CREATE TABLE IF NOT EXISTS tenants (
    id                BIGSERIAL      PRIMARY KEY,
    user_id           BIGINT         NOT NULL,
    room_id           BIGINT,
    full_name         VARCHAR(150)   NOT NULL,
    email             VARCHAR(255)   NOT NULL UNIQUE,
    phone             VARCHAR(20)    NOT NULL,
    emergency_contact VARCHAR(20),
    join_date         DATE           NOT NULL DEFAULT CURRENT_DATE,
    security_deposit  DECIMAL(10,2)  NOT NULL DEFAULT 0,
    status            VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE'
                      CHECK (status IN ('ACTIVE','INACTIVE','PENDING')),
    created_at        TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_tenants_user_id  ON tenants(user_id);
CREATE INDEX IF NOT EXISTS idx_tenants_room_id  ON tenants(room_id);
CREATE INDEX IF NOT EXISTS idx_tenants_status   ON tenants(status);
CREATE INDEX IF NOT EXISTS idx_tenants_email    ON tenants(email);
