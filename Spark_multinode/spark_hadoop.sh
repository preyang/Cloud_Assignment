
#Below command getting spark version 2.2.0 from cloudfront 

wget https://d3kbcqa49mib13.cloudfront.net/spark-2.2.0-bin-hadoop2.7.tgz

#Now we are untaring the spark that we get from above command in /opt folder" 

sudo tar zxvf spark-2.2.0-bin-hadoop2.7.tgz -C /opt


#below command is used to access spark form anywhere within system 

sudo ln -fs spark-2.2.0-bin-hadoop2.7 /opt/spark

#set enviornment variable $SPARK_HOME in bashrc file

sudo cat >> ~/.bashrc <<EOF
export SPARK_HOME=/opt/spark
EOF

#updating bashrc file to save changes in the bashrc
source ~/.bashrc

#check if its spark is installed properly or not with this command and it will also show version of spark.
spark-submit --version

## we make changes to spark-defaults.conf file and set /spark_temp in mounted disk and do process in that folder so all spark computation will be in 
## that spark_temp file.
cp /opt/spark/conf/spark-defaults.conf.template /opt/spark/conf/spark-defaults.conf

sudo cat >> /opt/spark/conf/spark-defaults.conf <<EOF

spark.local.dir	/mnt/raid/spark_temp
EOF