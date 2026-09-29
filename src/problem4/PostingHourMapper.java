package problem4;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PostingHourMapper
        extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final Text hour = new Text();
    private final IntWritable one = new IntWritable(1);

    private static final Pattern NEW_TIMESTAMP =
            Pattern.compile("(?m)^Timestamp:\\s*(\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}):\\d{2}:\\d{2}");

    @Override
    protected void map(
            LongWritable key,
            Text value,
            Context context)
            throws IOException, InterruptedException {

        String post = value.toString().trim();

        if (post.isEmpty())
            return;

        String postingHour = null;

        /*
         * -----------------------------------------
         * OLD FORMAT
         *
         * p00001|User0328|2026-09-04T00:47:17|...
         * -----------------------------------------
         */

        if (post.contains("|")) {

            String[] lines =
                    post.split("\\r?\\n");

            if (lines.length == 0)
                return;

            String metadata =
                    lines[0].trim();

            String[] fields =
                    metadata.split("\\|");

            if (fields.length < 3)
                return;

            String timestamp =
                    fields[2].trim();

            if (timestamp.length() < 13)
                return;

            postingHour =
                    timestamp.substring(11, 13);
        }

        /*
         * -----------------------------------------
         * NEW FORMAT
         *
         * Timestamp: 2026-03-05 07:21:00
         * -----------------------------------------
         */

        else {

            Matcher timestampMatcher =
                    NEW_TIMESTAMP.matcher(post);

            if (!timestampMatcher.find())
                return;

            postingHour =
                    timestampMatcher.group(1)
                            .substring(11, 13);
        }

        if (postingHour == null)
            return;

        hour.set(postingHour);

        context.write(hour, one);
    }
}
