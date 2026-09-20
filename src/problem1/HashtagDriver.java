package problem1;

import inputformat.PostInputFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class HashtagDriver {

    public static void main(String[] args)
            throws Exception {

        Configuration conf =
                new Configuration();

        Job job =
                Job.getInstance(
                        conf,
                        "Most Frequent Hashtags");

        job.setJarByClass(
                HashtagDriver.class);

        job.setInputFormatClass(
                PostInputFormat.class);

        job.setMapperClass(
                HashtagMapper.class);

        job.setCombinerClass(
                HashtagCombiner.class);

        job.setReducerClass(
                HashtagReducer.class);

        job.setMapOutputKeyClass(
                Text.class);

        job.setMapOutputValueClass(
                IntWritable.class);

        job.setOutputKeyClass(
                Text.class);

        job.setOutputValueClass(
                IntWritable.class);

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
