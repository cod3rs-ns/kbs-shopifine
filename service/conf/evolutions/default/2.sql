# --- !Ups

ALTER TABLE users ADD COLUMN google_account_id VARCHAR(255);
ALTER TABLE users MODIFY address TEXT;

# --- !Downs

ALTER TABLE users DROP COLUMN google_account_id;
