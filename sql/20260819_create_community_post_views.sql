CREATE TABLE IF NOT EXISTS community_post_views (
  post_no BIGINT NOT NULL,
  viewer_key_hash CHAR(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  view_date DATE NOT NULL,
  created_at DATETIME(6) NOT NULL,
  UNIQUE KEY uk_community_post_views_post_viewer_date (post_no, viewer_key_hash, view_date),
  KEY idx_community_post_views_view_date (view_date),
  CONSTRAINT fk_community_post_views_post
    FOREIGN KEY (post_no) REFERENCES community_posts (no) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
