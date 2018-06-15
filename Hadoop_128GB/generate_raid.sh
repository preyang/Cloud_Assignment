
##This script mount the disk ar raid level and give permission to disk with label /mnt/raid

#install mdadm tool to mount disk
apt-get install mdadm

##In below line we give notation about disk that we want to mount ar RAID level
mdadm --create /dev/md0 --run --level=0 --name=RAID --force --raid-devices=1 /dev/nvme0n1
mkfs.ext4 -L RAID /dev/md0
mkdir -p /mnt/raid

##It will lable that disk as /mnt/raid
mount LABEL=RAID /mnt/raid

##Give all permission to the /mnt/raid to access it in anyway.
chmod 777 /mnt/raid
