package problem5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class HashtagPairMapper
        extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final Text pair = new Text();
    private final IntWritable one = new IntWritable(1);

    private static final Pattern HASHTAG =
            Pattern.compile("#[A-Za-z0-9_]+");

    @Override
    protected void map(
            LongWritable key,
            Text value,
            Context context)
            throws IOException, InterruptedException {

        String post = value.toString().trim();

        Matcher matcher =
                HASHTAG.matcher(post);

        Set<String> uniqueTags =
                new HashSet<>();

        while (matcher.find()) {
            uniqueTags.add(
                    matcher.group().toLowerCase());
        }

        ArrayList<String> tags =
                new ArrayList<>(uniqueTags);

        Collections.sort(tags);

        for (int i = 0; i < tags.size(); i++) {

            for (int j = i + 1;
                 j < tags.size();
                 j++) {

                pair.set(
                        tags.get(i) + "," + tags.get(j));

                context.write(pair, one);
            }
        }
    }
}
