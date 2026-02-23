CREATE TABLE integrations
(
    id            UUID           NOT NULL,
    is_deleted    BOOLEAN        NOT NULL,
    deleted_at    TIMESTAMP WITHOUT TIME ZONE,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255),
    merchant_id   UUID           NOT NULL,
    platform      VARCHAR(255)   NOT NULL,
    api_key       VARCHAR(255)   NOT NULL,
    shop_name     VARCHAR(255)   NOT NULL,
    currency      VARCHAR(3)     NOT NULL,
    exchange_rate DECIMAL(18, 6) NOT NULL,
    CONSTRAINT pk_integrations PRIMARY KEY (id)
);

CREATE TABLE merchants
(
    id           UUID         NOT NULL,
    is_deleted   BOOLEAN      NOT NULL,
    deleted_at   TIMESTAMP WITHOUT TIME ZONE,
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    created_by   VARCHAR(255),
    updated_by   VARCHAR(255),
    company_name VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    CONSTRAINT pk_merchants PRIMARY KEY (id)
);

CREATE TABLE order_item_adjustments
(
    id            UUID       NOT NULL,
    is_deleted    BOOLEAN    NOT NULL,
    deleted_at    TIMESTAMP WITHOUT TIME ZONE,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    updated_at    TIMESTAMP WITHOUT TIME ZONE,
    created_by    VARCHAR(255),
    updated_by    VARCHAR(255),
    order_item_id UUID       NOT NULL,
    type          SMALLINT   NOT NULL,
    reason        VARCHAR(255),
    amount        BIGINT     NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    CONSTRAINT pk_order_item_adjustments PRIMARY KEY (id)
);

CREATE TABLE order_items
(
    id                          UUID       NOT NULL,
    is_deleted                  BOOLEAN    NOT NULL,
    deleted_at                  TIMESTAMP WITHOUT TIME ZONE,
    created_at                  TIMESTAMP WITHOUT TIME ZONE,
    updated_at                  TIMESTAMP WITHOUT TIME ZONE,
    created_by                  VARCHAR(255),
    updated_by                  VARCHAR(255),
    order_id                    UUID       NOT NULL,
    product_id                  UUID       NOT NULL,
    quantity                    INTEGER    NOT NULL,
    selling_price_amount        BIGINT     NOT NULL,
    selling_price_currency_code VARCHAR(3) NOT NULL,
    platform_fee_amount         BIGINT     NOT NULL,
    platform_fee_currency_code  VARCHAR(3) NOT NULL,
    logistics_fee_amount        BIGINT     NOT NULL,
    logistics_fee_currency_code VARCHAR(3) NOT NULL,
    other_fees_amount           BIGINT     NOT NULL,
    other_fees_currency_code    VARCHAR(3) NOT NULL,
    payout_amount_amount        BIGINT     NOT NULL,
    payout_amount_currency_code VARCHAR(3) NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (id)
);

CREATE TABLE orders
(
    id             UUID         NOT NULL,
    is_deleted     BOOLEAN      NOT NULL,
    deleted_at     TIMESTAMP WITHOUT TIME ZONE,
    created_at     TIMESTAMP WITHOUT TIME ZONE,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    created_by     VARCHAR(255),
    updated_by     VARCHAR(255),
    integration_id UUID         NOT NULL,
    external_id    VARCHAR(255) NOT NULL,
    order_date     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status         VARCHAR(50)  NOT NULL,
    raw_data       JSONB,
    CONSTRAINT chk_orders_status CHECK (status IN ('CREATED', 'IN_TRANSIT', 'DELIVERED', 'RETURNED', 'CANCELLED')),
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

CREATE TABLE products
(
    id                       UUID         NOT NULL,
    is_deleted               BOOLEAN      NOT NULL,
    deleted_at               TIMESTAMP WITHOUT TIME ZONE,
    created_at               TIMESTAMP WITHOUT TIME ZONE,
    updated_at               TIMESTAMP WITHOUT TIME ZONE,
    created_by               VARCHAR(255),
    updated_by               VARCHAR(255),
    merchant_id              UUID         NOT NULL,
    sku                      VARCHAR(255) NOT NULL,
    name                     VARCHAR(255) NOT NULL,
    cost_price_amount        BIGINT       NOT NULL,
    cost_price_currency_code VARCHAR(3)   NOT NULL,
    CONSTRAINT pk_products PRIMARY KEY (id)
);

CREATE TABLE sync_jobs
(
    id             UUID     NOT NULL,
    created_at     TIMESTAMP WITHOUT TIME ZONE,
    updated_at     TIMESTAMP WITHOUT TIME ZONE,
    created_by     VARCHAR(255),
    updated_by     VARCHAR(255),
    integration_id UUID     NOT NULL,
    status         SMALLINT NOT NULL,
    started_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    finished_at    TIMESTAMP WITHOUT TIME ZONE,
    error_message  VARCHAR(255),
    CONSTRAINT pk_sync_jobs PRIMARY KEY (id)
);

ALTER TABLE orders
    ADD CONSTRAINT uc_orders_integration_id_external_id UNIQUE (integration_id, external_id);

ALTER TABLE products
    ADD CONSTRAINT uc_products_merchant_id_sku UNIQUE (merchant_id, sku);

ALTER TABLE merchants
    ADD CONSTRAINT uc_merchants_email UNIQUE (email);

ALTER TABLE integrations
    ADD CONSTRAINT FK_INTEGRATIONS_ON_MERCHANT FOREIGN KEY (merchant_id) REFERENCES merchants (id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_INTEGRATION FOREIGN KEY (integration_id) REFERENCES integrations (id);

ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE order_items
    ADD CONSTRAINT FK_ORDER_ITEMS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE order_item_adjustments
    ADD CONSTRAINT FK_ORDER_ITEM_ADJUSTMENTS_ON_ORDER_ITEM FOREIGN KEY (order_item_id) REFERENCES order_items (id);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_MERCHANT FOREIGN KEY (merchant_id) REFERENCES merchants (id);

ALTER TABLE sync_jobs
    ADD CONSTRAINT FK_SYNC_JOBS_ON_INTEGRATION FOREIGN KEY (integration_id) REFERENCES integrations (id);

CREATE INDEX idx_orders_integration_id ON orders (integration_id);
CREATE INDEX idx_orders_order_date ON orders (order_date);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_product_id ON order_items (product_id);
CREATE INDEX idx_integrations_merchant_id ON integrations (merchant_id);
CREATE INDEX idx_products_merchant_id ON products (merchant_id);
CREATE INDEX idx_sync_jobs_integration_id ON sync_jobs (integration_id);
CREATE INDEX idx_order_item_adjustments_order_item_id ON order_item_adjustments (order_item_id);