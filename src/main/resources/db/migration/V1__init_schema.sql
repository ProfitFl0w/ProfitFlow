CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE merchants (
                           id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           company_name    VARCHAR(255) NOT NULL,
                           email           VARCHAR(255) NOT NULL UNIQUE,
                           password        VARCHAR(255) NOT NULL,
                           created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_merchants_email ON merchants (email);

CREATE TABLE integrations (
                              id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              merchant_id     UUID         NOT NULL REFERENCES merchants(id) ON DELETE CASCADE,
                              platform        VARCHAR(50)  NOT NULL CHECK (platform IN ('KASPI', 'OZON', 'WB')),
                              api_key         VARCHAR(512) NOT NULL,
                              shop_name       VARCHAR(255) NOT NULL,
                              created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_integrations_merchant ON integrations (merchant_id);

CREATE TABLE products (
                          id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          merchant_id     UUID           NOT NULL REFERENCES merchants(id) ON DELETE CASCADE,
                          sku             VARCHAR(255)   NOT NULL,
                          name            VARCHAR(500)   NOT NULL,
                          cost_price      NUMERIC(10,2),
                          created_at      TIMESTAMPTZ    NOT NULL DEFAULT now(),

                          CONSTRAINT uq_products_merchant_sku UNIQUE (merchant_id, sku)
);

CREATE INDEX idx_products_merchant ON products (merchant_id);
CREATE INDEX idx_products_sku      ON products (sku);

CREATE TABLE orders (
                        id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        integration_id  UUID         NOT NULL REFERENCES integrations(id) ON DELETE CASCADE,
                        external_id     VARCHAR(255) NOT NULL,
                        order_date      TIMESTAMPTZ  NOT NULL,
                        status          VARCHAR(50)  NOT NULL CHECK (status IN ('CREATED', 'IN_TRANSIT', 'DELIVERED', 'RETURNED', 'CANCELLED')),
                        created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),

                        CONSTRAINT uq_orders_integration_external UNIQUE (integration_id, external_id)
);

CREATE INDEX idx_orders_integration       ON orders (integration_id);
CREATE INDEX idx_orders_date              ON orders (order_date);
CREATE INDEX idx_orders_integration_date  ON orders (integration_id, order_date);

CREATE TABLE order_items (
                             id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             order_id        UUID           NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                             product_id      UUID           NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
                             quantity        INT            NOT NULL DEFAULT 1 CHECK (quantity > 0),
                             selling_price   NUMERIC(10,2)  NOT NULL DEFAULT 0,
                             platform_fee    NUMERIC(10,2)  NOT NULL DEFAULT 0,
                             logistics_fee   NUMERIC(10,2)  NOT NULL DEFAULT 0,
                             other_fees      NUMERIC(10,2)  NOT NULL DEFAULT 0,
                             payout_amount   NUMERIC(10,2)  NOT NULL DEFAULT 0
);

CREATE INDEX idx_order_items_order   ON order_items (order_id);
CREATE INDEX idx_order_items_product ON order_items (product_id);