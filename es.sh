#!/bin/bash
文件描述符问题：
vi /etc/security/limits.conf

httpd soft nofile 4096
httpd hard nofile 10240

必须root登录修改不然不会起作用