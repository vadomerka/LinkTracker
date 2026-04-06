-- liquibase formatted sql

-- changeset author:002-2
ALTER TABLE link ADD COLUMN IF NOT EXISTS update_body VARCHAR;
