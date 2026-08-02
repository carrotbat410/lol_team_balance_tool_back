ALTER TABLE community_posts
    ADD COLUMN IF NOT EXISTS image_url varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL AFTER content,
    ADD COLUMN IF NOT EXISTS view_count bigint NOT NULL DEFAULT 0 AFTER writer_id;

ALTER TABLE community_settings
    ADD COLUMN IF NOT EXISTS notice_display_count int NOT NULL DEFAULT 2 AFTER visible_to_users;
