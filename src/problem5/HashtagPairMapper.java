package problem5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class HashtagPairMapper
        extends Mapper<LongWritable, Text, Text, IntWritable> {

    private static final IntWritable ONE = new IntWritable(1);

    private final Text pair = new Text();

    @Override
    protected void map(
            LongWritable key,
            Text value,
            Context context)
            throws IOException, InterruptedException {

        String post = value.toString().trim();

        String[] lines = post.split("\\r?\\n");

        if (lines.length == 0) {
            return;
        }

        String[] fields = lines[0].trim().split("\\|");

        /*
         * Dataset format:
         *
         * 0 = post ID
         * 1 = username
         * 2 = timestamp
         * 3 = hashtags
         * 4 = likes
         * 5 = shares
         */

        if (fields.length < 4) {
            return;
        }

        String hashtagField = fields[3].trim();

        if (hashtagField.isEmpty()) {
            return;
        }

        String[] rawHashtags = hashtagField.split(",");

        /*
         * Remove duplicate hashtags within
         * the same post.
         */
        Set<String> unique =
                new HashSet<String>();

        for (String hashtag : rawHashtags) {

            hashtag = hashtag.trim().toLowerCase();

            if (!hashtag.isEmpty()) {
                unique.add(hashtag);
            }
        }

        /*
         * Convert to list and sort.
         *
         * Sorting guarantees that:
         *
         * #ai,#hadoop
         *
         * and
         *
         * #hadoop,#ai
         *
         * are treated as the same pair.
         */
        List<String> hashtags =
                new ArrayList<String>(unique);

        Collections.sort(hashtags);

        /*
         * Generate all unique combinations.
         */
        for (int i = 0; i < hashtags.size(); i++) {

            for (int j = i + 1;
                 j < hashtags.size();
                 j++) {

                pair.set(
                        hashtags.get(i)
                        + ","
                        + hashtags.get(j)
                );

                context.write(pair, ONE);
            }
        }
    }
}