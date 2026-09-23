raw = LOAD 'hdfs://namenode:9000/chatterwave/hive_input/posts_metadata.tsv'
      USING PigStorage('|')
      AS (
          post_id:chararray,
          username:chararray,
          post_time:chararray,
          hashtags:chararray,
          likes:chararray,
          shares:chararray
      );

tokens = FOREACH raw
         GENERATE FLATTEN(TOKENIZE(hashtags, ',')) AS hashtag;

grouped = GROUP tokens BY hashtag;

counts = FOREACH grouped
         GENERATE
             group AS hashtag,
             COUNT(tokens) AS frequency;

ordered = ORDER counts BY frequency DESC, hashtag ASC;

STORE ordered
INTO 'hdfs://namenode:9000/chatterwave/output/problem6_pig'
USING PigStorage('\t');
