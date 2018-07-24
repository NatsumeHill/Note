#/bin/bash


echo "Adding the MySQL Yum Repository"
echo "================================"

wget -P /tmp https://repo.mysql.com//mysql80-community-release-el7-1.noarch.rpm

sudo yum localinstall /tmp/mysql80-community-release-el7-1.noarch.rpm -y

echo "Installing MySQL"
echo "==============================="

sudo yum install mysql-community-server -y

echo "add mysql to firewall"
echo "==============================="

sudo firewall-cmd --zone=public --add-service=mysql --permanent
sudo firewall-cmd --reload

echo " Start mysql by command:"

echo "sudo service mysqld start"

echo "root default pass by command:"

echo "sudo grep 'temporary password' /var/log/mysqld.log"

echo "change pass by mysql command:"

echo "mysql> ALTER USER 'root'@'localhost' IDENTIFIED BY 'MyNewPass4!';"