CREATE TABLE IF NOT EXISTS rent_records (
    id            BIGSERIAL      PRIMARY KEY,
    tenant_id     BIGINT         NOT NULL,
    room_id       BIGINT         NOT NULL,
    rent_month    INTEGER        NOT NULL CHECK (rent_month BETWEEN 1 AND 12),
    rent_year     INTEGER        NOT NULL,
    rent_amount   DECIMAL(10,2)  NOT NULL,
    total_paid    DECIMAL(10,2)  NOT NULL DEFAULT 0,
    status        VARCHAR(20)    NOT NULL DEFAULT 'DUE'
                  CHECK (status IN ('DUE','PARTIAL','PAID')),
    created_at    TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP      NOT NULL DEFAULT NOW(),
    UNIQUE (tenant_id, rent_month, rent_year)
);

CREATE TABLE IF NOT EXISTS payments (
    id              BIGSERIAL      PRIMARY KEY,
    rent_record_id  BIGINT         NOT NULL REFERENCES rent_records(id),
    amount_paid     DECIMAL(10,2)  NOT NULL,
    payment_mode    VARCHAR(20)    NOT NULL
                    CHECK (payment_mode IN ('CASH','UPI','BANK_TRANSFER','CHEQUE')),
    reference_number VARCHAR(100),
    payment_date    TIMESTAMP      NOT NULL DEFAULT NOW(),
    created_at      TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_rent_records_tenant  ON rent_records(tenant_id);
CREATE INDEX IF NOT EXISTS idx_rent_records_status  ON rent_records(status);
CREATE INDEX IF NOT EXISTS idx_payments_record      ON payments(rent_record_id);
