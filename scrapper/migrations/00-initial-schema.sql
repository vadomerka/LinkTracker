-- liquibase formatted sql

-- changeset author:001-1
CREATE TABLE IF NOT EXISTS chat (
    chat_id BIGINT PRIMARY KEY NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- changeset author:001-2
CREATE TABLE IF NOT EXISTS link (
    url TEXT PRIMARY KEY NOT NULL UNIQUE,
    last_update TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- changeset author:001-3
CREATE TABLE IF NOT EXISTS tag (
    name VARCHAR(255) PRIMARY KEY NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- changeset author:001-4
CREATE TABLE IF NOT EXISTS chat_link (
    chat_id BIGINT NOT NULL,
    link_url TEXT NOT NULL,
    PRIMARY KEY (chat_id, link_url),
    FOREIGN KEY (chat_id) REFERENCES chat(chat_id) ON DELETE CASCADE,
    FOREIGN KEY (link_url) REFERENCES link(url) ON DELETE CASCADE
);

-- changeset author:001-5
CREATE TABLE IF NOT EXISTS link_tag (
    link_url TEXT NOT NULL,
    tag_name TEXT NOT NULL,
    PRIMARY KEY (link_url, tag_name),
    FOREIGN KEY (link_url) REFERENCES link(url) ON DELETE CASCADE,
    FOREIGN KEY (tag_name) REFERENCES tag(name) ON DELETE CASCADE
);

-- changeset author:001-6
--CREATE INDEX IF NOT EXISTS idx_chat_link_link_id ON chat_link(link_id);
--CREATE INDEX IF NOT EXISTS idx_link_tag_tag_id ON link_tag(tag_id);
