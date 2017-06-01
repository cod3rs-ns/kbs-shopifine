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

# --- !Downs
DROP TABLE IF EXISTS buyer_categories;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS product_categories;
DROP TABLE IF EXISTS products;
