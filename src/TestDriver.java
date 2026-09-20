import inputformat.PostInputFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TestDriver {

    public static void main(String[] args)
            throws Exception {

        Configuration conf =
                new Configuration();

        Job job =
                Job.getInstance(
                        conf,
                        "Custom InputFormat Test");

        job.setJarByClass(TestDriver.class);

        job.setInputFormatClass(
                PostInputFormat.class);

        job.setMapperClass(
                TestMapper.class);

        job.setMapOutputKeyClass(
                Text.class);

        job.setMapOutputValueClass(
                Text.class);

        job.setNumReduceTasks(0);

        PostInputFormat.addInputPath(
                job,
                new Path(args[0]));

        FileOutputFormat.setOutputPath(
                job,
                new Path(args[1]));

        System.exit(
                job.waitForCompletion(true)
                        ? 0 : 1);
    }
}

