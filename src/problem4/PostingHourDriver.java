package problem4;

import inputformat.PostInputFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PostingHourDriver {

    public static void main(String[] args)
            throws Exception {

        if (args.length != 2) {
            System.err.println(
                    "Usage: PostingHourDriver <input> <output>");
            System.exit(1);
        }

        Configuration conf =
                new Configuration();

        Job job =
                Job.getInstance(
                        conf,
                        "ChatterWave Problem 4 - Posting Frequency by Hour");

        job.setJarByClass(
                PostingHourDriver.class);

        job.setInputFormatClass(
                PostInputFormat.class);

        job.setMapperClass(
                PostingHourMapper.class);

        job.setCombinerClass(
                PostingHourCombiner.class);

        job.setReducerClass(
                PostingHourReducer.class);

        job.setMapOutputKeyClass(
                Text.class);

        job.setMapOutputValueClass(
                IntWritable.class);

        job.setOutputKeyClass(
                Text.class);

        job.setOutputValueClass(
                IntWritable.class);

        FileInputFormat.addInputPath(
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
