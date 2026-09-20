package problem3;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class MaxMapper
        extends Mapper<LongWritable, Text, Text, Text> {

    private final Text maxKey = new Text("MAX");
    private final Text result = new Text();

    @Override
    protected void map(
            LongWritable key,
            Text value,
            Context context)
            throws IOException, InterruptedException {

        String line = value.toString().trim();

        if (line.isEmpty())
            return;

        String[] parts = line.split("\\s+");

        if (parts.length < 2)
            return;

        String user = parts[0];
        String engagement = parts[1];

        result.set(user + "," + engagement);

        context.write(maxKey, result);
    }
}
