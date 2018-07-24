#!/bin/bash
set -e
echo "Uninstall old versions"
echo "======================"
sudo yum remove docker \
docker-client \
docker-client-latest \
docker-common \
docker-latest \
docker-latest-logrotate \
docker-logrotate \
docker-selinux \
docker-engine-selinux \
docker-engine

echo "Install required packages"
echo "========================="
sudo yum install -y yum-utils \
device-mapper-persistent-data \
lvm2

echo "set up the stable repository"
echo "============================"
sudo yum-config-manager \
--add-repo \
https://download.docker.com/linux/centos/docker-ce.repo

sudo sed -i 's+download.docker.com+mirrors.tuna.tsinghua.edu.cn/docker-ce+' /etc/yum.repos.d/docker-ce.repo

echo "Install the latest version of Docker CE"
echo "========================================"
sudo yum install docker-ce