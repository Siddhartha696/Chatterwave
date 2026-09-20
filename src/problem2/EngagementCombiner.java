package problem2;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class EngagementCombiner
        extends Reducer<Text, Text, Text, Text> {

    private Text result = new Text();

    @Override
    protected void reduce(
            Text key,
            Iterable<Text> values,
            Context context)
            throws IOException, InterruptedException {

        double total = 0;
        int count = 0;

        for (Text value : values) {

            String[] parts =
                    value.toString().split(",");

            total +=
                    Double.parseDouble(parts[0]);

            count +=
                    Integer.parseInt(parts[1]);
        }

        result.set(total + "," + count);

        context.write(key, result);
    }
}
