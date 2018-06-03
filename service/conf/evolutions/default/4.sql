# --- !Ups

ALTER TABLE bills ADD COLUMN address TEXT;
ALTER TABLE bills ADD COLUMN longitude DOUBLE;
ALTER TABLE bills ADD COLUMN latitude DOUBLE;

# --- !Downs

ALTER TABLE bills DROP COLUMN address;
ALTER TABLE bills DROP COLUMN longitude;
ALTER TABLE bills DROP COLUMN latitude;
