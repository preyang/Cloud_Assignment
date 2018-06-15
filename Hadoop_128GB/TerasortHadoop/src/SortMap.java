import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

/***************************************************
* SortMap task will run the mapper of the hadoop
* Here Map method will take Record as input and convert 
* Each record into <Key(substring(0,10)),value(substring(10,100)))
****************************************************/

public class SortMap extends Mapper<LongWritable, Text, Text, Text> {

	@Override
	protected void map(LongWritable key, Text value,Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
		Text key1 = new Text();
		//setting up the key value
		key1.set(value.toString().substring(0, 10));
		Text word = new Text();
		//setting up value
		word.set(value.toString().substring(10,value.toString().length()));
		context.write(key1, word);
	}

}
