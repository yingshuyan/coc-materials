CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,

    group_id BIGINT NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_users_group
        FOREIGN KEY (group_id)
        REFERENCES user_groups(id)
);

CREATE TABLE storage_areas (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE storage_locations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,

    storage_area_id BIGINT NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_storage_locations_storage_area
        FOREIGN KEY (storage_area_id)
        REFERENCES storage_areas(id)
);

CREATE TABLE raw_materials (
    id BIGSERIAL PRIMARY KEY,

    product_code VARCHAR(255) NOT NULL,
    corning_part VARCHAR(255),
    lot_number VARCHAR(255) NOT NULL,
    material_type VARCHAR(255) NOT NULL,
    class VARCHAR(255) NOT NULL,
    form VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,

    quantity NUMERIC(12, 3) NOT NULL
       CHECK (quantity >= 0),

    uom VARCHAR(255) NOT NULL,
    notes TEXT,

    supplier_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    storage_location_id BIGINT NOT NULL,

    date_in TIMESTAMPTZ NOT NULL,
    date_out TIMESTAMPTZ,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_raw_materials_supplier
       FOREIGN KEY (supplier_id)
           REFERENCES suppliers(id),

    CONSTRAINT fk_raw_materials_owner
       FOREIGN KEY (owner_id)
           REFERENCES users(id),

    CONSTRAINT fk_raw_materials_storage_location
       FOREIGN KEY (storage_location_id)
           REFERENCES storage_locations(id),

    CONSTRAINT chk_raw_materials_dates
       CHECK (date_out IS NULL OR date_out >= date_in),

    CONSTRAINT uq_raw_materials_supplier_lot
       UNIQUE (supplier_id, lot_number)
);

CREATE INDEX idx_users_group_id
    ON users(group_id);

CREATE INDEX idx_storage_locations_storage_area_id
    ON storage_locations(storage_area_id);

CREATE INDEX idx_raw_materials_owner_id
    ON raw_materials(owner_id);

CREATE INDEX idx_raw_materials_storage_location_id
    ON raw_materials(storage_location_id);