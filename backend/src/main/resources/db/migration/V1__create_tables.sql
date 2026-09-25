CREATE TABLE sellers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT sellers_status_check
        CHECK (status IN ('APPROVED', 'PENDING', 'REJECTED'))
);


CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    unit VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE seller_listings (
    id BIGSERIAL PRIMARY KEY,

    seller_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,

    price NUMERIC(12, 2) NOT NULL,
    stock INTEGER NOT NULL,
    minimum_order_quantity INTEGER NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_listing_seller
        FOREIGN KEY (seller_id)
        REFERENCES sellers(id),

    CONSTRAINT fk_listing_product
        FOREIGN KEY (product_id)
        REFERENCES products(id),

    CONSTRAINT seller_product_unique
        UNIQUE (seller_id, product_id),

    CONSTRAINT listing_price_check
        CHECK (price > 0),

    CONSTRAINT listing_stock_check
        CHECK (stock >= 0),

    CONSTRAINT listing_moq_check
        CHECK (minimum_order_quantity > 0),

    CONSTRAINT listing_status_check
        CHECK (status IN ('ACTIVE', 'STOPPED'))
);


CREATE INDEX idx_products_name
    ON products(name);

CREATE INDEX idx_products_category
    ON products(category);

CREATE INDEX idx_listings_product
    ON seller_listings(product_id);

CREATE INDEX idx_listings_seller
    ON seller_listings(seller_id);

CREATE INDEX idx_listings_status
    ON seller_listings(status);