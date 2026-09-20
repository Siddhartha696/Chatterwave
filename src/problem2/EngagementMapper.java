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

    private static final Pattern LIKES_PATTERN =
            Pattern.compile("likes=(\\d+)");

    private static final Pattern SHARES_PATTERN =
            Pattern.compile("shares=(\\d+)");

    @Override
    protected void map(
            LongWritable key,
            Text value,
            Context context)
            throws IOException, InterruptedException {

        String post = value.toString();

        Matcher likesMatcher =
                LIKES_PATTERN.matcher(post);

        Matcher sharesMatcher =
                SHARES_PATTERN.matcher(post);

        if (!likesMatcher.find() ||
            !sharesMatcher.find())
            return;

        int likes =
                Integer.parseInt(
                        likesMatcher.group(1));

        int shares =
                Integer.parseInt(
                        sharesMatcher.group(1));

        int engagement =
                likes + shares;

        result.set(engagement + ",1");

        Matcher hashtagMatcher =
                HASHTAG_PATTERN.matcher(post);

        while (hashtagMatcher.find()) {

            hashtag.set(
                    hashtagMatcher.group().toLowerCase());

            context.write(
                    hashtag,
                    result);
        }
    }
}
