CREATE TABLE IF NOT EXISTS community_posts (
  no BIGINT NOT NULL AUTO_INCREMENT,
  category VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  title VARCHAR(160) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  content TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  image_url VARCHAR(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  writer_id VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  view_count BIGINT NOT NULL DEFAULT 0,
  created_at DATETIME(6) DEFAULT NULL,
  updated_at DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (no),
  KEY idx_community_posts_category_no (category, no),
  KEY idx_community_posts_writer_id (writer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
