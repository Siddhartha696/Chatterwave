package problem1;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class HashtagMapper
        extends Mapper<LongWritable, Text, Text, IntWritable> {

    private static final IntWritable ONE =
            new IntWritable(1);

    private Text hashtag = new Text();

    private static final Pattern HASHTAG_PATTERN =
            Pattern.compile("#[A-Za-z0-9_]+");

    @Override
    protected void map(
            LongWritable key,
            Text value,
            Context context)
            throws IOException, InterruptedException {

        String post = value.toString();

        Matcher matcher =
                HASHTAG_PATTERN.matcher(post);

        while (matcher.find()) {

            hashtag.set(
                    matcher.group().toLowerCase());

            context.write(
                    hashtag,
                    ONE);
        }
    }
}
