package problem3;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class EngagementMapper
        extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final Text user = new Text();
    private final IntWritable engagement = new IntWritable();

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

        // Expected:
        // 0 = post ID
        // 1 = username
        // 2 = timestamp
        // 3 = hashtags
        // 4 = likes
        // 5 = shares

        if (fields.length < 6)
            return;

        try {

            String username = fields[1].trim();

            int likes = Integer.parseInt(fields[4].trim());
            int shares = Integer.parseInt(fields[5].trim());

            int totalEngagement = likes + shares;

            user.set(username);
            engagement.set(totalEngagement);

            context.write(user, engagement);

        } catch (NumberFormatException e) {
            // Ignore malformed records
        }
    }
}