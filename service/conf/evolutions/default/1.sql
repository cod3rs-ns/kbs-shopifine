--
-- Initial Schema
--

# --- !Ups
CREATE TABLE buyer_categories (
  id              INT NOT NULL AUTO_INCREMENT,
  name            VARCHAR(40) NOT NULL,

  PRIMARY KEY (id)
);

CREATE TABLE users (
  id                  INT NOT NULL AUTO_INCREMENT,
  username            VARCHAR(40) NOT NULL,
  password            VARCHAR(40) NOT NULL,
  first_name          VARCHAR(40) NOT NULL,
  last_name           VARCHAR(40) NOT NULL,
  role                VARCHAR(15) NOT NULL,
  address             VARCHAR(40) NULL,
  buyer_category_id   INT NULL,
  points              INT NULL,
  registered_at       DATETIME NOT NULL,

  PRIMARY KEY (id)
);

ALTER TABLE
  users
ADD CONSTRAINT
  fk_users_buyer_categories_id
FOREIGN KEY (buyer_category_id) REFERENCES buyer_categories(id);

CREATE TABLE product_categories (
  id                  INT NOT NULL AUTO_INCREMENT,
  name                VARCHAR(40) NOT NULL,
  super_category_id   INT NULL,
  max_discount        DOUBLE NOT NULL,
  is_consumer_goods   BOOLEAN NOT NULL DEFAULT FALSE,

  PRIMARY KEY (id)
);

ALTER TABLE
  product_categories
ADD CONSTRAINT
  fk_product_categories_product_categories_id
FOREIGN KEY (super_category_id) REFERENCES product_categories(id);

CREATE TABLE products (
  id                    INT NOT NULL AUTO_INCREMENT,
  name                  VARCHAR(40) NOT NULL,
  image_url             VARCHAR(255) NOT NULL DEFAULT "",
  product_category_id   INT NOT NULL,
  price                 DOUBLE NOT NULL,
  quantity              INT NOT NULL,
  created_at            DATETIME NOT NULL,
  last_bought_at        DATETIME,
  fill_stock            BOOLEAN NOT NULL,
  status                VARCHAR(15) NOT NULL,
  min_quantity          INT NOT NULL,

  PRIMARY KEY (id)
);

ALTER TABLE
  products
ADD CONSTRAINT
  fk_products_product_categories_id
FOREIGN KEY (product_category_id) REFERENCES product_categories(id);

CREATE TABLE bills (
  id              INT NOT NULL AUTO_INCREMENT,
  created_at      DATETIME NOT NULL,
  customer        INT NOT NULL,
  state           VARCHAR(15) NOT NULL,
  total_items     INT NOT NULL,
  amount          DOUBLE NOT NULL,
  discount        DOUBLE NOT NULL,
  discount_amount DOUBLE NOT NULL,
  points_spent    INT NOT NULL,
  points_gained   INT NOT NULL,

  PRIMARY KEY (id)
);

ALTER TABLE
  bills
ADD CONSTRAINT
  fk_bills_users_id
FOREIGN KEY (customer) REFERENCES users(id);

CREATE TABLE bill_items (
  id              INT NOT NULL AUTO_INCREMENT,
  ordinal         INT DEFAULT 1 NOT NULL,
  product_id      INT NOT NULL,
  bill_id         INT NOT NULL,
  price           DOUBLE NOT NULL,
  quantity        INT NOT NULL,
  amount          DOUBLE NOT NULL,
  discount        DOUBLE NOT NULL,
  discount_amount DOUBLE NOT NULL,

  PRIMARY KEY (id)
);

ALTER TABLE
  bill_items
ADD CONSTRAINT
  fk_bill_items_products_id
FOREIGN KEY (product_id) REFERENCES products(id);

ALTER TABLE
  bill_items
ADD CONSTRAINT
  fk_bill_items_bills_id
FOREIGN KEY (bill_id) REFERENCES bills(id);

CREATE TRIGGER trigger_update_bill_item_ordinal
  BEFORE INSERT ON bill_items
FOR EACH ROW BEGIN
  SET NEW.ordinal = (SELECT COUNT(*) FROM bill_items WHERE bill_id = NEW.bill_id) + 1;;
END;

CREATE TABLE bill_discounts (
  id              INT NOT NULL AUTO_INCREMENT,
  bill_id         INT NOT NULL,
  discount        DOUBLE NOT NULL,
  discount_type   VARCHAR(15) NOT NULL,

  PRIMARY KEY (id)
);

ALTER TABLE
  bill_discounts
ADD CONSTRAINT
  fk_bill_discounts_bills_id
FOREIGN KEY (bill_id) REFERENCES bills(id);

CREATE TABLE item_discounts (
  id              INT NOT NULL AUTO_INCREMENT,
  item_id         INT NOT NULL,
  discount        DOUBLE NOT NULL,
  discount_type   VARCHAR(15) NOT NULL,

  PRIMARY KEY (id)
);

ALTER TABLE
  item_discounts
ADD CONSTRAINT
 fk_item_discounts_items_id
FOREIGN KEY (item_id) REFERENCES bill_items(id);

CREATE TABLE consumption_thresholds (
  id                  INT NOT NULL AUTO_INCREMENT,
  buyer_category_id   INT NOT NULL,
  `from`              INT NOT NULL,
  `to`                INT NOT NULL,
  award               DOUBLE NOT NULL,

  PRIMARY KEY (id)
);

ALTER TABLE
  consumption_thresholds
ADD CONSTRAINT
  fk_consumption_thresholds_buyer_categories_id
FOREIGN KEY (buyer_category_id) REFERENCES buyer_categories(id);

CREATE TABLE action_discounts (
  id              INT NOT NULL AUTO_INCREMENT,
  name            VARCHAR(40) NOT NULL,
  `from`          DATETIME NOT NULL,
  `to`            DATETIME NOT NULL,
  discount         DOUBLE NOT NULL,

  PRIMARY KEY (id)
);

CREATE TABLE action_discounts_product_categories (
  id              INT NOT NULL AUTO_INCREMENT,
  discount_id     INT NOT NULL,
  category_id     INT NOT NULL,

  PRIMARY KEY (id)
);

ALTER TABLE
  action_discounts_product_categories
ADD CONSTRAINT
  fk_action_discounts_product_categories_action_discounts_id
FOREIGN KEY (discount_id) REFERENCES action_discounts(id);

ALTER TABLE
  action_discounts_product_categories
ADD CONSTRAINT
  fk_action_discounts_product_categories_product_categories_id
FOREIGN KEY (category_id) REFERENCES product_categories(id);

# --- !Downs
DROP TABLE IF EXISTS buyer_categories;
DROP TABLE IF EXISTS users;
ALTER TABLE users DROP FOREIGN KEY fk_users_buyer_categories_id;
DROP TABLE IF EXISTS product_categories;
ALTER TABLE product_categories DROP FOREIGN KEY fk_product_categories_product_categories_id;
DROP TABLE IF EXISTS products;
ALTER TABLE products DROP FOREIGN KEY fk_products_product_categories_id;
DROP TABLE IF EXISTS bills;
ALTER TABLE bills DROP FOREIGN KEY fk_bills_users_id;
DROP TABLE IF EXISTS bill_items;
DROP TRIGGER IF EXISTS trigger_update_bill_item_ordinal;
ALTER TABLE bill_items DROP FOREIGN KEY fk_bill_items_products_id;
ALTER TABLE bill_items DROP FOREIGN KEY fk_bill_items_bills_id;
DROP TABLE IF EXISTS bill_discounts;
ALTER TABLE bill_discounts DROP FOREIGN KEY fk_bill_discounts_bills_id;
DROP TABLE IF EXISTS item_discounts;
ALTER TABLE item_discounts DROP FOREIGN KEY fk_item_discounts_items_id;
DROP TABLE IF EXISTS consumption_thresholds;
DROP TABLE IF EXISTS action_discounts;
ALTER TABLE action_discounts_product_categories DROP FOREIGN KEY fk_action_discounts_product_categories_action_discounts_id;
ALTER TABLE action_discounts_product_categories DROP FOREIGN KEY fk_action_discounts_product_categories_product_categories_id;
DROP TABLE IF EXISTS action_discounts_product_categories;
