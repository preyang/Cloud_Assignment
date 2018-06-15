import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
/***************************************************
* SortReducer task will run the Reducer of the hadoop
* Here Reduce method will take intermediate map task output as input  
****************************************************/
public class SortReducer extends Reducer<Text,Text,Text,Text> {

	@Override
	//reduce will take map task's result as input and perform write
	protected void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		 for(Text val:values){
	        	context.write(key, val);
	        }
	}

}
