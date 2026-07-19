CREATE TABLE IF NOT EXISTS visitor_logs (
  no BIGINT NOT NULL AUTO_INCREMENT,
  visit_date DATE NOT NULL,
  visitor_key VARCHAR(160) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  visitor_id VARCHAR(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  user_id VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  first_path VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  first_visited_at DATETIME(6) NOT NULL,
  last_visited_at DATETIME(6) NOT NULL,
  hit_count INT NOT NULL,
  PRIMARY KEY (no),
  UNIQUE KEY uk_visitor_logs_date_key (visit_date, visitor_key),
  KEY idx_visitor_logs_visit_date (visit_date),
  KEY idx_visitor_logs_user_id (user_id),
  KEY idx_visitor_logs_visitor_key (visitor_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
