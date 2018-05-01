# --- !Ups

ALTER TABLE users ADD COLUMN google_account_id VARCHAR(255);

# --- !Downs

ALTER TABLE users DROP COLUMN google_account_id;
