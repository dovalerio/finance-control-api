-- =============================================================
-- Finance Control API - Database Schema
-- =============================================================

CREATE TABLE IF NOT EXISTS categories (
    id   BIGSERIAL    PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS subcategories (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    category_id BIGINT       NOT NULL,
    CONSTRAINT fk_subcategories_category
        FOREIGN KEY (category_id)
        REFERENCES categories (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS entries (
    id             BIGSERIAL      PRIMARY KEY,
    description    VARCHAR(255)   NOT NULL,
    amount         NUMERIC(19, 2) NOT NULL,
    type           VARCHAR(10)    NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    date           DATE           NOT NULL,
    category_id    BIGINT         NOT NULL,
    subcategory_id BIGINT,
    CONSTRAINT fk_entries_category
        FOREIGN KEY (category_id)
        REFERENCES categories (id),
    CONSTRAINT fk_entries_subcategory
        FOREIGN KEY (subcategory_id)
        REFERENCES subcategories (id)
);

CREATE INDEX IF NOT EXISTS idx_entries_date          ON entries (date);
CREATE INDEX IF NOT EXISTS idx_entries_category_id   ON entries (category_id);
CREATE INDEX IF NOT EXISTS idx_entries_date_category ON entries (date, category_id);
