# --- !Ups

CREATE TABLE wishlist_items (
  id          INT      NOT NULL AUTO_INCREMENT,
  customer_id INT      NOT NULL,
  product_id  INT      NOT NULL,
  created_at  DATETIME NOT NULL,

  PRIMARY KEY (id)
);

ALTER TABLE
wishlist_items
  ADD CONSTRAINT
  fk_wishlist_items_customer_id
FOREIGN KEY (customer_id) REFERENCES users (id);

ALTER TABLE
wishlist_items
  ADD CONSTRAINT
  fk_wishlist_items_product_id
FOREIGN KEY (product_id) REFERENCES products (id);

# --- !Downs

DROP TABLE wishlist_items;
