package problem2;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class EngagementReducer
        extends Reducer<Text, Text, Text, DoubleWritable> {

    private DoubleWritable result =
            new DoubleWritable();

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

        double average =
                total / count;

        result.set(
                Math.round(average * 100.0) / 100.0);

        context.write(
                key,
                result);
    }
}
