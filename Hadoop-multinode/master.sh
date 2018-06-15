

##we need to go to etc folder for putting host details from hosts_tmp file in every instance.
cd  /etc/

# Give permission to the hosts folder so that we can access it without any problem
sudo chmod 777 hosts


sudo cat /home/ubuntu/hosts_tmp >> hosts


##Generate the ssh key and add it to authorized_keys under ssh of instance. This will useful for password-less login in the slave instance.
ssh-keygen -t rsa -P ""

cat $HOME/.ssh/id_rsa.pub >> $HOME/.ssh/authorized_keys

## Now set max = number of Slave instance.
max=8
## Now using scp command we are putting ssh key that we generted and hosts_tmp file to slave.
## After sending that 2 file to all slave we are going to that instance using ssh and stored ssh key to authorized key.
## WE follow this step for all slave insatance. 
for i in `seq 1 $max`
do
	scp -i /home/ubuntu/preyang_multi.pem $HOME/.ssh/id_rsa.pub ubuntu@hadoop-slave-$i:/home/ubuntu/master.pub
	scp -i /home/ubuntu/preyang_multi.pem /home/ubuntu/hosts_tmp ubuntu@hadoop-slave-$i:/home/ubuntu/hosts_tmp
	ssh -i /home/ubuntu/preyang_multi.pem ubuntu@hadoop-slave-$i '
	sudo su <<EOF
	sudo cat /home/ubuntu/master.pub >> /root/.ssh/authorized_keys
	EOF'
done

##Now install java and hadoop in master instance.
 
sudo apt-get purge openjdk-\* -y  

echo "Now we will update repository..."  

sudo apt-get update -y  

echo "Adding Java Repository...."  

sudo apt-get install python-software-properties -y  
sudo add-apt-repository ppa:webupd8team/java -y  

echo "Updating Repository to load java repository"  

sudo apt-get update -y  

echo "Installing Sun Java....."  
sudo -E apt-get purge oracle-java8-installer -y 
echo debconf shared/accepted-oracle-license-v1-1 select true | sudo debconf-set-selections
echo debconf shared/accepted-oracle-license-v1-1 seen true | sudo debconf-set-selections 
sudo -E apt-get install oracle-java8-installer -y  


echo "Installation completed...."  

echo "Installed java version is...."  

sudo java -version 

# install java in slave
# now using the same ssh we can do password less login and we put same command to run under all slave instance.
max=8
for i in `seq 1 $max`
do
	ssh hadoop-slave-$i '
	sudo apt-get purge openjdk-\* -y
	sudo apt-get update -y
	sudo apt-get install python-software-properties -y  
	sudo add-apt-repository ppa:webupd8team/java -y 
	sudo apt-get update -y
	sudo -E apt-get purge oracle-java8-installer -y
	echo debconf shared/accepted-oracle-license-v1-1 select true | sudo debconf-set-selections
	echo debconf shared/accepted-oracle-license-v1-1 seen true | sudo debconf-set-selections  
	sudo -E apt-get install oracle-java8-installer -y  
	sudo java -version 

	sudo cat >> ~/.bashrc <<EOF
	export HADOOP_HOME=/usr/local/hadoop  
	export HADOOP_MAPRED_HOME=/usr/local/hadoop  
	export HADOOP_COMMON_HOME=/usr/local/hadoop  
	export HADOOP_HDFS_HOME=/usr/local/hadoop  
	export YARN_HOME=/usr/local/hadoop  
	export HADOOP_COMMON_LIB_NATIVE_DIR=/usr/local/hadoop/lib/native  
	export JAVA_HOME=/usr/  
	export PATH=$PATH:/usr/local/hadoop/sbin:/usr/local/hadoop/bin:$JAVA_PATH/bin  
	'
	ssh hadoop-slave-$i '
	cd  /etc/
	sudo chmod 777 hosts
	sudo cat /home/ubuntu/hosts_tmp >> hosts
	

	source ~/.bashrc'
done

###########################################################################################
#Now configre hadoop in master using below command and add property to its hadoop folder.

###########################################################################################
cd /usr/local 
sudo rm hadoop-2.7.1.tar.* 
sudo wget https://archive.apache.org/dist/hadoop/core/hadoop-2.7.1/hadoop-2.7.1.tar.gz
 
sudo tar xzf hadoop-2.7.1.tar.gz  
sudo rm -r hadoop
sudo mkdir hadoop  
sudo mv hadoop-2.7.1/* hadoop/  

echo "Now script is updating Bashrc for export Path etc"  

sudo cat >> ~/.bashrc <<EOF
export HADOOP_HOME=/usr/local/hadoop  
export HADOOP_MAPRED_HOME=/usr/local/hadoop  
export HADOOP_COMMON_HOME=/usr/local/hadoop  
export HADOOP_HDFS_HOME=/usr/local/hadoop  
export YARN_HOME=/usr/local/hadoop  
export HADOOP_COMMON_LIB_NATIVE_DIR=/usr/local/hadoop/lib/native  
export JAVA_HOME=/usr/  
export PATH=$PATH:/usr/local/hadoop/sbin:/usr/local/hadoop/bin:/bin  
EOF

#sudo cat ~/.bashrc

source ~/.bashrc

cd /usr/local/hadoop/etc/hadoop

echo "Now script is updating hadoop configuration files"  
sudo chmod 777 hadoop-env.sh
sudo cat >> hadoop-env.sh << EOL
export JAVA_HOME=/usr/  
EOL

sudo chmod 777 core-site.xml
sudo cat > core-site.xml << EOL
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>  
<property>  
<name>fs.default.name</name>  
<value>hdfs://hadoop-master:9000</value>  
</property>
<property>  
<name>dfs.permissions</name>  
<value>false</value>  
</property>  
<property>
<name>hadoop.tmp.dir</name>
<value>file:///mnt/raid/tmp/mapred/local</value>
</property>
</configuration>  
EOL

sudo chmod 777 hdfs-site.xml
sudo cat > hdfs-site.xml << EOL
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>  
<property>  
<name>dfs.replication</name>  
<value>1</value>  
</property>  
<property>  
<name>dfs.name.dir</name>  
<value>file:///mnt/raid/dfs/hadoop/hadoopinfra/hdfs/namenode</value>  
</property>  
<property>  
<name>dfs.data.dir</name>  
<value>file:///mnt/raid/dfs/hadoop/hadoopinfra/hdfs/datanode</value >  
</property>  
<property>  
<name>dfs.block.size</name>  
<value>1073741824</value >  
</property> 
</configuration>  
EOL

sudo chmod 777 mapred-site.xml.template
sudo cp mapred-site.xml.template mapred-site.xml
sudo chmod 777 mapred-site.xml
sudo cat > mapred-site.xml << EOL
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>  
<property>  
<name>mapreduce.framework.name</name>  
<value>yarn</value>  
</property>  
<property>  
<name>mapreduce.job.reduces</name>  
<value>8</value>  
</property>  

</configuration>  
EOL

sudo chmod 777 yarn-site.xml
sudo cat > yarn-site.xml << EOL
<?xml version="1.0"?>
<configuration>
<!-- Site specific YARN configuration properties -->
<property>
 <name>yarn.nodemanager.aux-services</name>
 <value>mapreduce_shuffle</value>
</property>
<property>
 <name>yarn.resourcemanager.scheduler.address</name>
 <value>hadoop-master:8030</value>
</property> 
<property>
 <name>yarn.resourcemanager.address</name>
 <value>hadoop-master:8032</value>
</property>
<property>
 <name>yarn.resourcemanager.webapp.address</name>
 <value>hadoop-master:8088</value>
</property>
<property>
 <name>yarn.resourcemanager.resource-tracker.address</name>
 <value>hadoop-master:8031</value>
</property>
<property>
 <name>yarn.resourcemanager.admin.address</name>
 <value>hadoop-master:8033</value>
</property>
</configuration> 
EOL

cd $HADOOP_HOME/etc/hadoop/
sudo chmod 777 $HADOOP_HOME/etc/hadoop/masters
sudo cat > masters << EOL
hadoop-master
EOL
sudo chmod 777 $HADOOP_HOME/etc/hadoop/masters


##Now give permission to /etc/hadoop folder so it can be accesible without restriction.
cd $HADOOP_HOME/etc/hadoop/
sudo chmod 777 $HADOOP_HOME/etc/hadoop/slaves
sudo cat > slaves << EOL
hadoop-slave-1
hadoop-slave-2
hadoop-slave-3
hadoop-slave-4
hadoop-slave-5
hadoop-slave-6
hadoop-slave-7
hadoop-slave-8
EOL

##Go to slave and put hadoop folder in all slave so that all slave has hadoop in it.Here Max= number of slave instances
cd /usr/local/
max=8
for i in `seq 1 $max`
do
	scp -r hadoop hadoop-slave-$i:/usr/local/
done



