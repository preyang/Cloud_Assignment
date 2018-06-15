
## This below for loop is send generate_raid.sh to all slave using scp command. 
max=8
for i in `seq 1 $max`
do
	scp -r generate_raid.sh spark-slave-$i:/home/ubuntu
done


