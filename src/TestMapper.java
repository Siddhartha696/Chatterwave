import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class TestMapper
        extends Mapper<LongWritable, Text, Text, Text> {

    @Override
    protected void map(
            LongWritable key,
            Text value,
            Context context)
            throws IOException, InterruptedException {

        String post = value.toString();

        String postId =
                post.split("\\|")[0];

        context.write(
                new Text(postId),
                new Text("POST_READ_SUCCESS"));
    }
}
