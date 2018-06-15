hadoop_128GB: 
	hadoop jar "TerasortHadoop" /mnt/raid/input_128gb /output_128gb

hadoop_1TB: 
	hadoop jar "TerasortHadoop" /mnt/raid/input_1TB /output_128gb



spark_128GB : 
	spark-shell -i spark_sort.txt

spark_1TB : 
	spark-shell -i spark_sort.txt



Shared_Memory_Sort_128GB : 
	java -Xms512m -Xmx11000m ExternalSort.jar /mnt/raid/input_128GB /mnt/raid/output_128GB 2

Shared_Memory_Sort_1TB : 
	java -Xms33000m -Xmx120000m ExternalSort.jar /mnt/raid/input_1TB /mnt/raid/output_1TB 16