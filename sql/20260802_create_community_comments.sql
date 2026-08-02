CREATE TABLE IF NOT EXISTS community_comments (
    no BIGINT NOT NULL AUTO_INCREMENT,
    post_no BIGINT NOT NULL,
    content TEXT NOT NULL,
    writer_id VARCHAR(255) NOT NULL,
    created_at DATETIME(6) DEFAULT NULL,
    updated_at DATETIME(6) DEFAULT NULL,
    PRIMARY KEY (no),
    INDEX idx_community_comments_post_no (post_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
