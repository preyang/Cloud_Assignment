import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DivideSortAndSave extends Thread {
	
	// tmp array for sorting
	private String[] tmpPassedArray;

	//creatting copy of passed chunk of array
	public DivideSortAndSave(String[] passedArray) {
		tmpPassedArray = new String[passedArray.length];
		// creating deep copy
		for (int i=0;i<passedArray.length;i++){
			if(passedArray[i] !=null && passedArray[i].length() > 10)
				tmpPassedArray[i] = passedArray[i];
		}
	}
	
	//Thread Class run method 
	public void run() {

		//calling mergeSort on created tmp array
		mergeSort(tmpPassedArray);
		
		//opening filewriter
		File tmpWriteFilePointer = null;
		try {
			tmpWriteFilePointer = File.createTempFile("externalSortfile", null,new File("/mnt/raid/tmp/"));
		
			tmpWriteFilePointer.deleteOnExit();
			BufferedWriter fbw = new BufferedWriter(new FileWriter(tmpWriteFilePointer));
			for (int i=0;i<tmpPassedArray.length;i++) {
				fbw.write(tmpPassedArray[i]);
				fbw.newLine();
			}
			fbw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//adding tmp file into globle tmpfile list for merging phase...
		ExternalSortMultiTheaded.tmpFilesList.add(tmpWriteFilePointer);
	}
	
	/**********************************************************************************
	 * Below are the function for the merge sort
	 * We are doing sorting by considering first 10 bytes fo the each records.
	 *********************************************************************************/
	public void mergeSort(String[] tmpPassed) {
        if (tmpPassed.length > 1) {
        	//System.out.println("in worker mergesort...");
        	String[] left = leftHalf(tmpPassed);
        	String[] right = rightHalf(tmpPassed);

            mergeSort(left);
            mergeSort(right);

            merge(tmpPassed, left, right);
        }
    }

    public String[] leftHalf(String[] tmpLeft) {
        int size1 = tmpLeft.length / 2;
        String[] left = new String[size1];
        for (int i = 0; i < size1; i++) {
            left[i] = tmpLeft[i];
        }
        return left;
    }

    public String[] rightHalf(String[] tmpRight) {
        int size1 = tmpRight.length / 2;
        int size2 = tmpRight.length - size1;
        String[] right = new String[size2];
        for (int i = 0; i < size2; i++) {
            right[i] = tmpRight[i + size1];
        }
        return right;
    }

    public void merge(String[] tmpFinal, String[] left, String[] right) {
        int i1 = 0;   
        int i2 = 0;   

        for (int i = 0; i < tmpFinal.length; i++) {
            if (i2 >= right.length || (i1 < left.length && left[i1].substring(0, 10).compareTo(right[i2].substring(0, 10))<0)) {
            	tmpFinal[i] = left[i1];   
                i1++;
            } else {
            	tmpFinal[i] = right[i2];   
                i2++;
            }
        }
    }
	
		
}