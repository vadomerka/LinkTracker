-- liquibase formatted sql

-- changeset author:003-1
CREATE TABLE IF NOT EXISTS outbox_message (
    id UUID PRIMARY KEY NOT NULL,
    topic VARCHAR(255) NOT NULL,
    message_key VARCHAR(255),
    payload TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP WITH TIME ZONE,
    error_message TEXT
);

-- changeset author:003-2
CREATE INDEX IF NOT EXISTS idx_outbox_unprocessed ON outbox_message (created_at) WHERE processed_at IS NULL;
