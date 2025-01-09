#!/bin/bash
# yum -y install wget && wget https://raw.githubusercontent.com/xnx3/translate/refs/heads/master/deploy/tcdn_install.sh -O ~/install.sh && chmod -R 777 ~/install.sh && sh ~/install.sh
#
yum -y install wget
yum -y install unzip

# 校验down.zvo.cn下载源的通畅
wget https://gitee.com/HuaweiCloudDeveloper/huaweicloud-solution-build-wangmarketcms/raw/master/shell/hosts.sh -O ~/hosts.sh && chmod -R 777 ~/hosts.sh &&  sh ~/hosts.sh
rm -rf ~/hosts.sh

# 安装 redis
wget https://gitee.com/HuaweiCloudDeveloper/huaweicloud-solution-build-wangmarketcms/raw/master/shell/redis_no_install.sh -O ~/redis.sh && chmod -R 777 ~/redis.sh &&  sh ~/redis.sh
rm -rf ~/redis.sh

# 下载应用程序
mkdir /mnt 
mkdir /mnt/key
mkdir /mnt/key/logs
mkdir /mnt/key/bin
mkdir /mnt/key/bin/jre8
cd /mnt/key/bin/jre8/
wget http://down.zvo.cn/centos/jre8.zip -O jre8.zip
unzip jre8.zip
rm -rf jre8.zip
cd /mnt/key/bin/
wget http://down.zvo.cn/key/bin/api.jar -O api.jar
wget http://down.zvo.cn/key/bin/admin.jar -O admin.jar
wget http://down.zvo.cn/key/bin/database.db -O database.db
cd /mnt/key/
wget http://down.zvo.cn/key/config.properties -O config.properties
wget http://down.zvo.cn/key/start-api.sh -O start-api.sh
wget http://down.zvo.cn/key/start-admin.sh -O start-admin.sh

chmod -R 777 /mnt/key/start-api.sh
chmod -R 777 /mnt/key/start-admin.sh

# 加入开机自启动
echo 'cd /mnt/key/ && sh start-api.sh'>>/etc/rc.d/rc.local
echo 'cd /mnt/key/ && sh start-admin.sh'>>/etc/rc.d/rc.local
# 赋予可执行权限
chmod +x /mnt/key/start-admin.sh
chmod +x /mnt/key/start-api.sh
chmod +x /etc/rc.d/rc.local


# 关闭防火墙
systemctl stop firewalld
systemctl disable firewalld

# 启动
cd /mnt/key/
sh start-api.sh
sh start-admin.sh