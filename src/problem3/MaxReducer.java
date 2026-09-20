package problem3;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class MaxReducer
        extends Reducer<Text, Text, Text, Text> {

    private String maxUser = "";
    private int maxEngagement = -1;

    @Override
    protected void reduce(
            Text key,
            Iterable<Text> values,
            Context context)
            throws IOException, InterruptedException {

        for (Text value : values) {

            String[] parts =
                    value.toString().split(",");

            if (parts.length < 2)
                continue;

            String user = parts[0];
            int engagement =
                    Integer.parseInt(parts[1]);

            if (engagement > maxEngagement) {
                maxEngagement = engagement;
                maxUser = user;
            }
        }

        context.write(
                new Text(maxUser),
                new Text(String.valueOf(maxEngagement)));
    }
}
