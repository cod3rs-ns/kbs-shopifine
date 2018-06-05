# --- !Ups

ALTER TABLE users ADD COLUMN longitude DOUBLE;
ALTER TABLE users ADD COLUMN latitude DOUBLE;

# --- !Downs

ALTER TABLE users DROP COLUMN longitude;
ALTER TABLE users DROP COLUMN latitude;
