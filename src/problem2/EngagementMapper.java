package problem2;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class EngagementMapper
        extends Mapper<LongWritable, Text, Text, Text> {

    private Text hashtag = new Text();
    private Text result = new Text();

    private static final Pattern HASHTAG_PATTERN =
            Pattern.compile("#[A-Za-z0-9_]+");

    // Original ChatterWave format:
    // likes=123
    // shares=45
    private static final Pattern OLD_LIKES_PATTERN =
            Pattern.compile("likes=(\\d+)");

    private static final Pattern OLD_SHARES_PATTERN =
            Pattern.compile("shares=(\\d+)");

    // New multi-line format:
    // Likes: 123
    // Shares: 45
    private static final Pattern NEW_LIKES_PATTERN =
            Pattern.compile("Likes:\\s*(\\d+)");

    private static final Pattern NEW_SHARES_PATTERN =
            Pattern.compile("Shares:\\s*(\\d+)");

    @Override
    protected void map(
            LongWritable key,
            Text value,
            Context context)
            throws IOException, InterruptedException {

        String post = value.toString();

        int likes;
        int shares;

        // -----------------------------------------
        // Try original format first
        // -----------------------------------------

        Matcher likesMatcher =
                OLD_LIKES_PATTERN.matcher(post);

        Matcher sharesMatcher =
                OLD_SHARES_PATTERN.matcher(post);

        if (likesMatcher.find() &&
            sharesMatcher.find()) {

            likes =
                    Integer.parseInt(
                            likesMatcher.group(1));

            shares =
                    Integer.parseInt(
                            sharesMatcher.group(1));

        } else {

            // -----------------------------------------
            // Try new multi-line format
            // -----------------------------------------

            likesMatcher =
                    NEW_LIKES_PATTERN.matcher(post);

            sharesMatcher =
                    NEW_SHARES_PATTERN.matcher(post);

            if (!likesMatcher.find() ||
                !sharesMatcher.find()) {

                return;
            }

            likes =
                    Integer.parseInt(
                            likesMatcher.group(1));

            shares =
                    Integer.parseInt(
                            sharesMatcher.group(1));
        }

        int engagement =
                likes + shares;

        result.set(
                engagement + ",1");

        // -----------------------------------------
        // Extract hashtags
        // -----------------------------------------

        Matcher hashtagMatcher =
                HASHTAG_PATTERN.matcher(post);

        while (hashtagMatcher.find()) {

            hashtag.set(
                    hashtagMatcher
                            .group()
                            .toLowerCase());

            context.write(
                    hashtag,
                    result);
        }
    }
}
