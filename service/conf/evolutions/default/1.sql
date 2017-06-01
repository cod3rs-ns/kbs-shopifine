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
  id              INT NOT NULL AUTO_INCREMENT,
  username        VARCHAR(40) NOT NULL,
  password        VARCHAR(40) NOT NULL,
  first_name      VARCHAR(40) NOT NULL,
  last_name       VARCHAR(40) NOT NULL,
  role            VARCHAR(15) NOT NULL,
  address         VARCHAR(40) NULL,
  buyer_category  INT NULL,
  points          INT NULL,
  registered_at   DATETIME NOT NULL,

  PRIMARY KEY (id)
  -- FOREIGN KEY (buyer_category) REFERENCES buyer_categories(buyer_category)
);

CREATE TABLE product_categories (
  id              INT NOT NULL AUTO_INCREMENT,
  name            VARCHAR(40) NOT NULL,
  super_category  INT NULL,
  max_discount    DOUBLE NOT NULL,

  PRIMARY KEY (id)
  -- FOREIGN KEY (super_category) REFERENCES product_categories(super_category)
);

CREATE TABLE products (
  id              INT NOT NULL AUTO_INCREMENT,
  name            VARCHAR(40) NOT NULL,
  category        INT NOT NULL,
  price           DOUBLE NOT NULL,
  quantity        INT NOT NULL,
  created_at      DATETIME NOT NULL,
  fill_stock      BOOLEAN NOT NULL,
  status          VARCHAR(15) NOT NULL,
  min_quantity    INT NOT NULL,

  PRIMARY KEY (id)
  -- FOREIGN KEY (category) REFERENCES product_categories(category)
);

CREATE TABLE bills (
  id              INT NOT NULL AUTO_INCREMENT,
  created_at      DATETIME NOT NULL,
  customer        INT NOT NULL,
  state           VARCHAR(15) NOT NULL,
  amount          DOUBLE NOT NULL,
  discount        DOUBLE NOT NULL,
  discount_amount DOUBLE NOT NULL,
  points_spent    INT NOT NULL,
  points_gained   INT NOT NULL,

  PRIMARY KEY (id)
  -- FOREIGN KEY (customer) REFERENCES users(customer)
);

CREATE TABLE bill_items (
  id              INT NOT NULL AUTO_INCREMENT,
  ordinal         INT NOT NULL,
  product         INT NOT NULL,
  bill            INT NOT NULL,
  price           DOUBLE NOT NULL,
  quantity        INT NOT NULL,
  amount          DOUBLE NOT NULL,
  discount        DOUBLE NOT NULL,
  discount_amount DOUBLE NOT NULL,

  PRIMARY KEY (id)
  -- FOREIGN KEY (product) REFERENCES products(product),
  -- FOREIGN KEY (bill) REFERENCES bills(bill)
);

CREATE TABLE bill_discounts (
  id              INT NOT NULL AUTO_INCREMENT,
  bill            INT NOT NULL,
  discount        DOUBLE NOT NULL,
  discount_type   VARCHAR(15) NOT NULL,

  PRIMARY KEY (id)
  -- FOREIGN KEY (bill) REFERENCES bills(bill)
);

CREATE TABLE item_discounts (
  id              INT NOT NULL AUTO_INCREMENT,
  item            INT NOT NULL,
  discount        DOUBLE NOT NULL,
  discount_type   VARCHAR(15) NOT NULL,

  PRIMARY KEY (id)
  -- FOREIGN KEY (item) REFERENCES bill_items(item)
);

# --- !Downs
DROP TABLE IF EXISTS buyer_categories;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS product_categories;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS bill_items;
DROP TABLE IF EXISTS bill_discounts;
DROP TABLE IF EXISTS item_discounts;
