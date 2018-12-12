import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class InvertedIndexJob {

	public static class TokenizerMapper extends Mapper<LongWritable, Text, Text, Text> {

		private Text word = new Text();
		Text docid = new Text();

		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

			String line = value.toString();
			StringTokenizer tokens = new StringTokenizer(line);
			String docidstr = tokens.nextToken();
			docid = new Text(docidstr);
			String[] arr = new String[100];
			while (tokens.hasMoreTokens()) {
				String str = tokens.nextToken();
				str = str.trim();
				/*if (str.contains("\'")) {
					StringBuilder sb = new StringBuilder(str);
					if (str.charAt(0) == '\'') {
						str = sb.deleteCharAt(0).toString();
					}
					if (str.length() > 0) {
						if (str.charAt(str.length() - 1) == '\'') {
							str = sb.deleteCharAt(str.length() - 1).toString();
						}
					}
				}*/

				/*
				 * str = str.replaceAll("[^A-Za-z\'\\s]", ""); while (str.length() > 0 &&
				 * (str.charAt(0) == '\'' || str.charAt(str.length() - 1) == '\'')) { str =
				 * str.replaceAll("\\B'\\b|\\b'\\B", ""); }
				 */
				str = str.toLowerCase().replaceAll("[^a-zA-Z\\s]", " ");
				arr = str.split("\\s+");
				for(int i=0;i<arr.length;i++) {
					str = arr[i].trim();
					word.set(str);
					context.write(word, docid);
				}
			}
		}
	}

	public static class IntSumReducer extends Reducer<Text, Text, Text, Text> {

		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

			String value;
			int count = 0;
			Iterator<Text> itr = values.iterator();
			HashMap<String, Integer> hashh = new HashMap<String, Integer>();

			while (itr.hasNext()) {
				value = itr.next().toString();
				if (hashh.containsKey(value)) {
					count = (hashh.get(value));
					count += 1;
					hashh.put(value, count);
				} else {
					hashh.put(value, 1);
				}

			}
			StringBuffer sb = new StringBuffer("");
			for (Map.Entry<String, Integer> map : hashh.entrySet()) {
				sb.append(map.getKey() + ":" + map.getValue() + "\t");

			}
			context.write(key, new Text(sb.toString()));

		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "inverted index");
		job.setJarByClass(InvertedIndexJob.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
