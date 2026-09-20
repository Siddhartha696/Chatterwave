package inputformat;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.util.LineReader;

public class PostRecordReader
        extends RecordReader<LongWritable, Text> {

    private LongWritable key = new LongWritable();
    private Text value = new Text();

    private LineReader reader;
    private long position;

    @Override
    public void initialize(
            InputSplit split,
            TaskAttemptContext context)
            throws IOException {

        FileSplit fileSplit = (FileSplit) split;

        Configuration conf = context.getConfiguration();

        FileSystem fs =
                fileSplit.getPath().getFileSystem(conf);

        FSDataInputStream in =
                fs.open(fileSplit.getPath());

        reader = new LineReader(in);

        position = fileSplit.getStart();
    }

    @Override
    public boolean nextKeyValue()
            throws IOException {

        StringBuilder post =
                new StringBuilder();

        Text line = new Text();

        while (true) {

            int bytes =
                    reader.readLine(line);

            if (bytes == 0) {

                if (post.length() == 0)
                    return false;

                break;
            }

            position += bytes;

            String text =
                    line.toString();

            if (text.trim().equals("###")) {

                if (post.length() > 0)
                    break;

            } else {

                post.append(text)
                    .append("\n");
            }
        }

        key.set(position);

        value.set(post.toString().trim());

        return true;
    }

    @Override
    public LongWritable getCurrentKey() {
        return key;
    }

    @Override
    public Text getCurrentValue() {
        return value;
    }

    @Override
    public float getProgress() {
        return 0.0f;
    }

    @Override
    public void close()
            throws IOException {

        if (reader != null)
            reader.close();
    }
}
