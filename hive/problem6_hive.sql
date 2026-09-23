DROP TABLE IF EXISTS raw_posts;

CREATE EXTERNAL TABLE raw_posts (
    post_id STRING,
    username STRING,
    post_time STRING,
    hashtags_raw STRING,
    likes_raw STRING,
    shares_raw STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '|'
STORED AS TEXTFILE
LOCATION 'hdfs://namenode:9000/chatterwave/hive_input';


DROP TABLE IF EXISTS posts_hive;

CREATE TABLE posts_hive (
    post_id STRING,
    username STRING,
    post_time STRING,
    hashtags ARRAY<STRING>,
    likes INT,
    shares INT
);


INSERT OVERWRITE TABLE posts_hive
SELECT
    post_id,
    username,
    post_time,
    split(hashtags_raw, ','),
    CAST(regexp_replace(likes_raw, '^likes=', '') AS INT),
    CAST(regexp_replace(shares_raw, '^shares=', '') AS INT)
FROM raw_posts;


SELECT
    hashtag,
    COUNT(*) AS frequency
FROM posts_hive
LATERAL VIEW explode(hashtags) h AS hashtag
GROUP BY hashtag
ORDER BY frequency DESC, hashtag ASC;
