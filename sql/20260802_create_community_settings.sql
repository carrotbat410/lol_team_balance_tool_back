CREATE TABLE IF NOT EXISTS community_settings (
  id INT NOT NULL,
  visible_to_users TINYINT(1) NOT NULL DEFAULT 0,
  notice_display_count INT NOT NULL DEFAULT 2,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO community_settings (id, visible_to_users, notice_display_count)
VALUES (1, 0, 2)
ON DUPLICATE KEY UPDATE visible_to_users = visible_to_users;
