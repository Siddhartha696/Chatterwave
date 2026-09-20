package problem3;

import inputformat.PostInputFormat;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class EngagementDriver {

    public static void main(String[] args)
            throws Exception {

        if (args.length != 3) {
            System.err.println(
                    "Usage: EngagementDriver <input> <stage1> <output>");
            System.exit(1);
        }

        Configuration conf =
                new Configuration();

        // -------------------------
        // STAGE 1
        // -------------------------

        Job job1 =
                Job.getInstance(
                        conf,
                        "ChatterWave Problem 3 - User Engagement");

        job1.setJarByClass(
                EngagementDriver.class);

        job1.setInputFormatClass(
                PostInputFormat.class);

        job1.setMapperClass(
                EngagementMapper.class);

        job1.setCombinerClass(
                EngagementCombiner.class);

        job1.setReducerClass(
                EngagementReducer.class);

        job1.setMapOutputKeyClass(
                Text.class);

        job1.setMapOutputValueClass(
                IntWritable.class);

        job1.setOutputKeyClass(
                Text.class);

        job1.setOutputValueClass(
                IntWritable.class);

        FileInputFormat.addInputPath(
                job1,
                new Path(args[0]));

        FileOutputFormat.setOutputPath(
                job1,
                new Path(args[1]));

        if (!job1.waitForCompletion(true))
            System.exit(1);

        // -------------------------
        // STAGE 2
        // -------------------------

        Job job2 =
                Job.getInstance(
                        conf,
                        "ChatterWave Problem 3 - Find Maximum");

        job2.setJarByClass(
                EngagementDriver.class);

        job2.setMapperClass(
                MaxMapper.class);

        job2.setReducerClass(
                MaxReducer.class);

        job2.setMapOutputKeyClass(
                Text.class);

        job2.setMapOutputValueClass(
                Text.class);

        job2.setOutputKeyClass(
                Text.class);

        job2.setOutputValueClass(
                Text.class);

        FileInputFormat.addInputPath(
                job2,
                new Path(args[1]));

        FileOutputFormat.setOutputPath(
                job2,
                new Path(args[2]));

        System.exit(
                job2.waitForCompletion(true)
                        ? 0 : 1);
    }
}
