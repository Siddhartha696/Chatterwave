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

    private static final Pattern LIKES =
            Pattern.compile("likes=(\\d+)");

    private static final Pattern SHARES =
            Pattern.compile("shares=(\\d+)");

    @Override
    protected void map(
            LongWritable key,
            Text value,
            Context context)
            throws IOException, InterruptedException {

        String post = value.toString().trim();

        String[] lines = post.split("\\r?\\n");

        if (lines.length == 0)
            return;

        String metadata = lines[0].trim();

        String[] fields = metadata.split("\\|");

        if (fields.length < 6)
            return;

        String username = fields[1].trim();

        Matcher likesMatcher = LIKES.matcher(metadata);
        Matcher sharesMatcher = SHARES.matcher(metadata);

        if (!likesMatcher.find() || !sharesMatcher.find())
            return;

        int likes = Integer.parseInt(likesMatcher.group(1));
        int shares = Integer.parseInt(sharesMatcher.group(1));

        int total = likes + shares;

        user.set(username);
        engagement.set(total);

        context.write(user, engagement);
    }
}
