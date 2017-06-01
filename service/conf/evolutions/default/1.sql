--
-- Initial Schema
--

# --- !Ups
CREATE TABLE users (
  id            INT NOT NULL AUTO_INCREMENT,
  username      VARCHAR(40) NOT NULL,
  password      VARCHAR(40) NOT NULL,
  first_name    VARCHAR(40) NOT NULL,
  last_name     VARCHAR(40) NOT NULL,
  role          VARCHAR(15) NOT NULL,
  registered_at DATETIME NOT NULL,

  PRIMARY KEY (id)
);

# --- !Downs
DROP TABLE IF EXISTS users;