# 编译安装 Nginx

## 依赖安装

PCRE 支持正则表达式，Nginx Core和Rewrite模块需要
```
$ wget ftp://ftp.csx.cam.ac.uk/pub/software/programming/pcre/pcre-8.42.tar.gz
$ tar -zxf pcre-8.42.tar.gz
$ cd pcre-8.42
$ ./configure
$ make
$ sudo make install
```
zlib – Supports header compression. Required by the NGINX Gzip module.
```
$ wget http://zlib.net/zlib-1.2.11.tar.gz
$ tar -zxf zlib-1.2.11.tar.gz
$ cd zlib-1.2.11
$ ./configure
$ make
$ sudo make install
```
OpenSSL – Supports the HTTPS protocol. Required by the NGINX SSL module and others.
```
$ wget http://www.openssl.org/source/openssl-1.0.2o.tar.gz
$ tar -zxf openssl-1.0.2o.tar.gz
$ cd openssl-1.0.2o
$ ./Configure darwin64-x86_64-cc --prefix=/usr
$ make
$ sudo make install
```
## Nginx编译

详细可以查看 ```https://docs.nginx.com/nginx/admin-guide/installing-nginx/installing-nginx-open-source/#prebuilt_redhat```