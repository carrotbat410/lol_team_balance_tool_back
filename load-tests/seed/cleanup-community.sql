SELECT COUNT(*) AS synthetic_posts_before
FROM community_posts
WHERE LEFT(title, 8) = '[K6-DEV]'
  AND LEFT(writer_id, 12) = 'k6_loadtest_';

SELECT COUNT(*) AS synthetic_comments_before
FROM community_comments AS c
INNER JOIN community_posts AS p ON p.no = c.post_no
WHERE LEFT(p.title, 8) = '[K6-DEV]'
  AND LEFT(p.writer_id, 12) = 'k6_loadtest_'
  AND LEFT(c.writer_id, 12) = 'k6_loadtest_';

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

COMMIT;

SELECT COUNT(*) AS synthetic_posts_after
FROM community_posts
WHERE LEFT(title, 8) = '[K6-DEV]'
  AND LEFT(writer_id, 12) = 'k6_loadtest_';

SELECT COUNT(*) AS synthetic_comments_after
FROM community_comments AS c
INNER JOIN community_posts AS p ON p.no = c.post_no
WHERE LEFT(p.title, 8) = '[K6-DEV]'
  AND LEFT(p.writer_id, 12) = 'k6_loadtest_'
  AND LEFT(c.writer_id, 12) = 'k6_loadtest_';
