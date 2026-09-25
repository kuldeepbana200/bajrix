ALTER TABLE seller_listings
ADD CONSTRAINT listing_moq_stock_check CHECK (minimum_order_quantity <= stock);