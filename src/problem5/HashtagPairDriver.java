package problem5;

import inputformat.PostInputFormat;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class HashtagPairDriver {

    public static void main(String[] args)
            throws Exception {

        if (args.length != 2) {

            System.err.println(
                    "Usage: HashtagPairDriver <input> <output>");

            System.exit(2);
        }

        Configuration conf =
                new Configuration();

        Job job = Job.getInstance(
                conf,
                "ChatterWave Problem 5 - Hashtag Pairs");

        job.setJarByClass(
                HashtagPairDriver.class);

        /*
         * Use the custom ChatterWave
         * multi-line record reader.
         */
        job.setInputFormatClass(
                PostInputFormat.class);

        job.setMapperClass(
                HashtagPairMapper.class);

        job.setReducerClass(
                HashtagPairReducer.class);

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
                ? 0
                : 1);
    }
}