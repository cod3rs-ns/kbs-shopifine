--
-- Initial Schema
--

# --- !Ups
CREATE TABLE buyer_categories (
  id              INT NOT NULL AUTO_INCREMENT,
  name            VARCHAR(40) NOT NULL
);

CREATE TABLE users (
  id              INT NOT NULL AUTO_INCREMENT,
  username        VARCHAR(40) NOT NULL,
  password        VARCHAR(40) NOT NULL,
  first_name      VARCHAR(40) NOT NULL,
  last_name       VARCHAR(40) NOT NULL,
  role            VARCHAR(15) NOT NULL,
  address         VARCHAR(40),
  buyer_category  INT REFERENCES buyer_categories(id) NULL,
  points          INT,
  registered_at   DATETIME NOT NULL,

  PRIMARY KEY (id)
);

# --- !Downs
DROP TABLE IF EXISTS users;