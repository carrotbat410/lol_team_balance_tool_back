DELIMITER //

DROP PROCEDURE IF EXISTS add_community_read_indexes//
CREATE PROCEDURE add_community_read_indexes()
BEGIN
    DECLARE comments_named_count INT DEFAULT 0;
    DECLARE comments_named_definition VARCHAR(255);
    DECLARE comments_equivalent_count INT DEFAULT 0;
    DECLARE posts_named_count INT DEFAULT 0;
    DECLARE posts_named_definition VARCHAR(255);
    DECLARE posts_equivalent_count INT DEFAULT 0;

    SELECT COUNT(*), GROUP_CONCAT(column_name ORDER BY seq_in_index)
    INTO comments_named_count, comments_named_definition
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'community_comments'
      AND index_name = 'idx_community_comments_post_no_no';

    IF comments_named_count > 0 AND comments_named_definition <> 'post_no,no' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'idx_community_comments_post_no_no has an unexpected definition';
    END IF;

    SELECT COUNT(*)
    INTO comments_equivalent_count
    FROM (
        SELECT index_name
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'community_comments'
          AND is_visible = 'YES'
        GROUP BY index_name
        HAVING GROUP_CONCAT(column_name ORDER BY seq_in_index) = 'post_no,no'
    ) AS equivalent_comments_indexes;

    IF comments_equivalent_count = 0 THEN
        ALTER TABLE community_comments
            ADD INDEX idx_community_comments_post_no_no (post_no, no);
    END IF;

    SELECT COUNT(*), GROUP_CONCAT(column_name ORDER BY seq_in_index)
    INTO posts_named_count, posts_named_definition
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'community_posts'
      AND index_name = 'idx_community_posts_category_no';

    IF posts_named_count > 0 AND posts_named_definition <> 'category,no' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'idx_community_posts_category_no has an unexpected definition';
    END IF;

    SELECT COUNT(*)
    INTO posts_equivalent_count
    FROM (
        SELECT index_name
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = 'community_posts'
          AND is_visible = 'YES'
        GROUP BY index_name
        HAVING GROUP_CONCAT(column_name ORDER BY seq_in_index) = 'category,no'
    ) AS equivalent_posts_indexes;

    IF posts_equivalent_count = 0 THEN
        ALTER TABLE community_posts
            ADD INDEX idx_community_posts_category_no (category, no);
    END IF;
END//

CALL add_community_read_indexes()//
DROP PROCEDURE add_community_read_indexes//

DELIMITER ;
