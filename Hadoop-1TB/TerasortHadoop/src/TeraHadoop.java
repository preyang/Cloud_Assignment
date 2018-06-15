import org.apache.hadoop.io.Text;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**************************************************************
*This is main class from where execution of hadoop map reduce start.
*Here in this calss we are setting up hadoop job configuration
*****************************************************************/
public class TeraHadoop {

	public static void main(String[] args) throws Exception {
		 	
		Path input = new Path(args[0]); //will take input file path
		Path output = new Path(args[1]); // will take output file path
		
		Configuration config = new Configuration();
		
		
		
		try{
			Job jb = Job.getInstance(config);
			//set input and output format
			FileInputFormat.setInputPaths(jb, input);
			//set input and output format
			FileOutputFormat.setOutputPath(jb, output);
		
			jb.setJobName("TeraHadoop"); //set Hadoop job name
			jb.setJarByClass(TeraHadoop.class); //set Main class to run
			jb.setInputFormatClass(TextInputFormat.class);//set Input format class
			jb.setOutputFormatClass(TextOutputFormat.class);//set output format class
			jb.setMapOutputKeyClass(Text.class);//set map output key Type class
			jb.setMapOutputValueClass(Text.class);//set value output key Type class
			jb.setOutputKeyClass(Text.class);
			jb.setOutputValueClass(Text.class);

			jb.setMapperClass(SortMap.class);//set mapper class
			jb.setCombinerClass(SortReducer.class);//set combiner class
			jb.setReducerClass(SortReducer.class);//set reducer class
			System.exit( jb.waitForCompletion(true) ? 0 : 1);
	  }

		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
		
}