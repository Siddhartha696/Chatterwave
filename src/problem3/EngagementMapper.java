package problem3;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class EngagementMapper
        extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final Text user = new Text();
    private final IntWritable engagement = new IntWritable();

    private static final Pattern OLD_LIKES =
            Pattern.compile("likes=(\\d+)");

    private static final Pattern OLD_SHARES =
            Pattern.compile("shares=(\\d+)");

    private static final Pattern NEW_USER =
            Pattern.compile("(?m)^User:\\s*(.+)$");

    private static final Pattern NEW_LIKES =
            Pattern.compile("(?m)^Likes:\\s*(\\d+)$");

    private static final Pattern NEW_SHARES =
            Pattern.compile("(?m)^Shares:\\s*(\\d+)$");

    @Override
    protected void map(
            LongWritable key,
            Text value,
            Context context)
            throws IOException, InterruptedException {

        String post = value.toString().trim();

        if (post.isEmpty())
            return;

        String username;
        int likes;
        int shares;

        /*
         * OLD FORMAT
         * p00001|User0328|timestamp|hashtags|likes=377|shares=26
         */
        if (post.contains("|")) {

            String[] lines = post.split("\\r?\\n");

            if (lines.length == 0)
                return;

            String metadata = lines[0].trim();

            String[] fields = metadata.split("\\|");

            if (fields.length < 6)
                return;

            username = fields[1].trim();

            Matcher likesMatcher =
                    OLD_LIKES.matcher(metadata);

            Matcher sharesMatcher =
                    OLD_SHARES.matcher(metadata);

            if (!likesMatcher.find() ||
                !sharesMatcher.find())
                return;

            likes =
                    Integer.parseInt(likesMatcher.group(1));

            shares =
                    Integer.parseInt(sharesMatcher.group(1));
        }

        /*
         * NEW FORMAT
         * User: Explorer053
         * Timestamp: ...
         * Likes: 287
         * Shares: 50
         * Text: ...
         */
        else {

            Matcher userMatcher =
                    NEW_USER.matcher(post);

            Matcher likesMatcher =
                    NEW_LIKES.matcher(post);

            Matcher sharesMatcher =
                    NEW_SHARES.matcher(post);

            if (!userMatcher.find() ||
                !likesMatcher.find() ||
                !sharesMatcher.find())
                return;

            username =
                    userMatcher.group(1).trim();

            likes =
                    Integer.parseInt(
                            likesMatcher.group(1));

            shares =
                    Integer.parseInt(
                            sharesMatcher.group(1));
        }

        int total = likes + shares;

        user.set(username);
        engagement.set(total);

        context.write(user, engagement);
    }
}
