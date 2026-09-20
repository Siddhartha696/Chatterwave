package problem2;

import inputformat.PostInputFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class EngagementDriver {

    public static void main(String[] args)
            throws Exception {

        Configuration conf =
                new Configuration();

        Job job =
                Job.getInstance(
                        conf,
                        "Average Engagement per Hashtag");

        job.setJarByClass(
                EngagementDriver.class);

        job.setInputFormatClass(
                PostInputFormat.class);

        job.setMapperClass(
                EngagementMapper.class);

        job.setCombinerClass(
                EngagementCombiner.class);

        job.setReducerClass(
                EngagementReducer.class);

        job.setMapOutputKeyClass(
                Text.class);

        job.setMapOutputValueClass(
                Text.class);

        job.setOutputKeyClass(
                Text.class);

        job.setOutputValueClass(
                DoubleWritable.class);

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

