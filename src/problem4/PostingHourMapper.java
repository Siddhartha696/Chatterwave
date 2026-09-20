package problem4;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PostingHourMapper
        extends Mapper<LongWritable, Text, Text, IntWritable> {

    private static final IntWritable ONE = new IntWritable(1);
    private final Text hour = new Text();

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

        if (fields.length < 6) {
            return;
        }

        String timestamp = fields[2].trim();

        /*
         * Expected timestamp:
         *
         * 2026-09-17 20:50:00
         *
         * HH starts at character position 11.
         */

        if (timestamp.length() < 13) {
            return;
        }

        String hh = timestamp.substring(11, 13);

        hour.set(hh);

        context.write(hour, ONE);
    }
}