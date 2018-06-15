import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/***********************************************************************
 * This External Sort algorithm can be ran in Multithreaded mode.
 * Threading can be performed during splitting and sorting phase 
 * In the merge phase files are written to single output file   
 **********************************************************************/

public class ExternalSortMultiTheaded {

	//global intermediate tmp file list
	static List<File> tmpFilesList = new ArrayList<File>();
	
	//global total number of recored read write
	static int totalRecord = 0;

	/*******************************************************************************
	 *This method will calculate available memory and run multiple thread
	 *they will work on different chunck and sort that chunk and store it into 
	 *It takes inputFile and number of thread as input parameter
	 *
	 **************************************************************************/
	public static void divideSortAndSave(File file, int thread) throws IOException {

		System.out.println("Creating buffer reader for input file");
		
		BufferedReader br = new BufferedReader(new FileReader(file));

		//getting available memory from system runtime
		long jvmAvailableSpace = Runtime.getRuntime().freeMemory();
		
		//Calculate the chunk size for every thread.
		long chunkSize = (jvmAvailableSpace / 2 );

		// temp list to store the input from large file and pass it to thread
		List<String> tmpChunkList = new ArrayList<String>();
		
		int noOfThreads = thread;
		Thread[] threadsArray = new Thread[noOfThreads];

		try {

			String line = "";
			try {
				while (line != null) { // file read loop
					
					for (int i = 0; i < threadsArray.length; i++) {  // loop for numner of thread
						long tempfilesize = 0;
						while (((line = br.readLine()) != null) && (tempfilesize < chunkSize)) { // loop to read tmp chunk for every thread
							tmpChunkList.add(line);
							totalRecord = totalRecord+1; // calculating total number of records read from input file
							tempfilesize += line.length()+2; // 2 for "/n" char.
						}

						if (tmpChunkList!=null && tmpChunkList.size()!=0) { 
							String[] passedArray = (String[]) tmpChunkList.toArray(new String[0]);
							//Creating Thread on DivideSortAndSave and pass chunk of data to every thread
							threadsArray[i] = new Thread(new DivideSortAndSave(passedArray));
							threadsArray[i].start();
							//removing tmp array from memory for space efficiency
							tmpChunkList = null;
						} 
						else {
							break;
						}
					}

					//Waiting for every thread to complete
					//here Thread join is not doing same work
					//so we need to make Main thread to sleep until all other thread to finish
					while (Thread.activeCount() != 1) {
							Thread.sleep(200);
					}
				}//end of while for all Line in input file

			} catch (Exception e) {
					e.printStackTrace();
			}
		} finally {
			br.close(); // closing the buffer reader
		}
	}
	
	/*****************************************************************************
	 * mergeAllFiles method will open buffer reader on all temporary file and 
	 * then start reading them and find mininum record and write it into final
	 * output file.
	 * 
	 ******************************************************************************/

	public static void mergeAllFiles(List<File> files, File outputfile) throws IOException {

		//define array of BufferReader which keep all files
		BufferedReader[] brs = new BufferedReader[files.size()];
		BufferedWriter fw = new BufferedWriter(new FileWriter(outputfile));
		
		//store single record of every file in array
		String[] singleRecordOfEachFile = new String[files.size()];
		int z=0;
		int size=0;
		
		//read first record from every file
		for (File f : files) {
			size += f.length();
			brs[z] = new BufferedReader(new FileReader(f));
			z++;
		}
		//System.out.println("size"+files.size());
		for(int i=0;i<files.size();i++){
			singleRecordOfEachFile[i] = brs[i].readLine();
		}
		//System.out.println("totalRecord"+totalRecord);
		
		int p=0;
		// here we are finding minimum value from all file and its index and 
		//then writing min record into final file		
		for (int j=0;j<totalRecord;j++)
		{
			String minRecord = singleRecordOfEachFile[0];
			int minFileIndex = 0;
			for(int k=0;k<files.size();k++){
				//System.out.println("k"+j);
				if(minRecord.substring(0, 10).compareTo(singleRecordOfEachFile[k].substring(0, 10)) > 0){ //find the least value among all the chunks and write it to the output file
					System.out.println(p);
					minRecord = singleRecordOfEachFile[k];
					minFileIndex = k;
				}
			}
			fw.write(minRecord);
			fw.write("\r\n");
			singleRecordOfEachFile[minFileIndex] = brs[minFileIndex].readLine();
		}
		
		fw.close(); //close the writing buffer
		
		//close all the buffer reader
		for (int i=0;i<brs.length;i++)
			brs[i].close();
		
	}

	/**********************************************************************
	 *  main method to start execution
	 * 1) It will call the divideSortSaveTmpFile which will sort chunk of data 
	 * and save sorted data into Temporary file.
	 * 2) Then it will call the mergeTempFile that will merge all temporary file. 
	 * *****************************************************************/
	public static void main(String[] args) throws IOException {
	
		 if(args.length != 3) {
           	    System.out.println("Please give input file, output file and number of thread as parameter...");
           	    System.out.println("Provide Commands Like: java -jar ExternalSort <inputfile> <outputfile> <No Of Threads>");
	            return;
        	 }

		long startTime, finishTime, elapsedTime;

		String inputfile =  args[0];;
		String outputfile =  args[1];
		int noOfThreads =  Integer.valueOf(args[2]);

		System.out.println("Start Dividing Single input file into chunks and sort then stores");
		
		//taking starting time
		startTime = System.currentTimeMillis();
		
		//divide the input file into chunk sort and save in tmp file
		divideSortAndSave(new File(inputfile),noOfThreads);
		System.out.println("Themparory Sorted File Generation Completed..");
		System.out.println("Intermediate File Merging phase start");
		//merge all temp file in single output file
		mergeAllFiles(tmpFilesList, new File(outputfile));
		
		//taking end time
		finishTime = System.currentTimeMillis();
		elapsedTime = finishTime - startTime;
		
		//calculate time in second
		float elapsedTimeSeconds = (float) (elapsedTime / 1000.0);

		System.out.println("Program Execution Finished.Do valsort and Check final result.");
		System.out.println("Time taken: " + elapsedTimeSeconds / 60 + " minutes");

	}
}
