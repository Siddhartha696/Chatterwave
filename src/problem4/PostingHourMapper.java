package problem4;

import inputformat.PostInputFormat;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PostingHourMapper
        extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final Text hour = new Text();
    private final IntWritable one = new IntWritable(1);

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

        if (fields.length < 3)
            return;

        String timestamp = fields[2].trim();

        if (timestamp.length() < 13)
            return;

        String postingHour = timestamp.substring(11, 13);

        hour.set(postingHour);

        context.write(hour, one);
    }
}
