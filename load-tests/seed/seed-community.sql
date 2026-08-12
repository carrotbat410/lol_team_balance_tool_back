START TRANSACTION;

DELETE c
FROM community_comments AS c
INNER JOIN community_posts AS p ON p.no = c.post_no
WHERE LEFT(p.title, 8) = '[K6-DEV]'
  AND LEFT(p.writer_id, 12) = 'k6_loadtest_'
  AND LEFT(c.writer_id, 12) = 'k6_loadtest_';

DELETE FROM community_posts
WHERE LEFT(title, 8) = '[K6-DEV]'
  AND LEFT(writer_id, 12) = 'k6_loadtest_';

INSERT INTO community_posts (
    category,
    title,
    content,
    image_url,
    writer_id,
    view_count,
    created_at,
    updated_at
)
WITH digits AS (
    SELECT 0 AS digit UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
),
numbers AS (
    SELECT ones.digit
         + tens.digit * 10
         + hundreds.digit * 100
         + thousands.digit * 1000
         + 1 AS n
    FROM digits AS ones
    CROSS JOIN digits AS tens
    CROSS JOIN digits AS hundreds
    CROSS JOIN digits AS thousands
)
SELECT CASE WHEN n <= 4000 THEN 'RECRUIT' ELSE 'NOTICE' END,
       CONCAT('[K6-DEV] ', CASE WHEN n <= 4000 THEN 'RECRUIT ' ELSE 'NOTICE ' END, LPAD(n, 4, '0')),
       CONCAT('Dev community read load-test fixture ', LPAD(n, 4, '0'), '.'),
       NULL,
       CONCAT('k6_loadtest_', LPAD(n, 4, '0')),
       0,
       NOW(6),
       NOW(6)
FROM numbers
WHERE n <= 5000;

INSERT INTO community_comments (
    post_no,
    content,
    writer_id,
    created_at,
    updated_at
)
SELECT p.no,
       CONCAT('K6 Dev load-test comment ', comment_numbers.n, ' for post ', p.no, '.'),
       CONCAT('k6_loadtest_comment_', comment_numbers.n),
       NOW(6),
       NOW(6)
FROM community_posts AS p
CROSS JOIN (
    SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
) AS comment_numbers
WHERE LEFT(p.title, 8) = '[K6-DEV]'
  AND LEFT(p.writer_id, 12) = 'k6_loadtest_';

COMMIT;

SELECT category, COUNT(*) AS seeded_posts
FROM community_posts
WHERE LEFT(title, 8) = '[K6-DEV]'
  AND LEFT(writer_id, 12) = 'k6_loadtest_'
GROUP BY category
ORDER BY category;

SELECT COUNT(*) AS seeded_comments
FROM community_comments AS c
INNER JOIN community_posts AS p ON p.no = c.post_no
WHERE LEFT(p.title, 8) = '[K6-DEV]'
  AND LEFT(p.writer_id, 12) = 'k6_loadtest_'
  AND LEFT(c.writer_id, 12) = 'k6_loadtest_';
