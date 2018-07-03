
# OpenWAF部署

本文是在CentOS下进行OpenWAF的部署

## 环境依赖安装

### 编译环境:
```
 yum install gcc gcc-c++ wget GeoIP-devel git swig make perl perl-ExtUtils-Embed readline-devel zlib-devel -y
```
### OpenSSL

OpenSSL版本需求， OpenResty 要求 OpenSSL 最低版本在 1.0.2e 以上，这里通过源码安装，从 ```https://www.openssl.org/source/``` 下载源码进行编译：
```
mkdir /usr/local/openssl
# from source root dir
./config --prefix=/usr/local/openssl
make
make install
```

### pcre-jit

OpenResty 依赖 PCRE，这里通过源码编译安装，从```https://jaist.dl.sourceforge.net/project/pcre/pcre/8.40/pcre-8.40.tar.gz```下载源码，然后解压：
```
# form source root dir
./configure --enable-jit  
make && make install
```
## 安装OpenWAF

下载OpenWAF源码，并将相关配置文件复制到指定目录，openresty编译时会使用
```
git clone https://github.com/titansec/OpenWAF.git
mv lib/openresty/ngx_openwaf.conf /etc
mv lib/openresty/configure ../openresty-1.11.2.2
cp -RP lib/openresty/* ../openresty-1.11.2.2/bundle/
make install
```

### 编译 openresty 

```
cd /opt/openresty-1.11.2.2/  
./configure --with-pcre-jit --with-ipv6 \  
            --with-http_stub_status_module \  
            --with-http_ssl_module \  
            --with-http_realip_module \  
            --with-http_sub_module  \  
            --with-http_geoip_module \  
            --with-openssl=/opt/openssl-1.0.2k \ 
            --with-pcre=/opt/pcre-8.40
make && make install 
```