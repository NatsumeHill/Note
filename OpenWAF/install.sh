#!/bin/bash
set -e
echo "===================="
echo "Install dependencies"

yum install gcc gcc-c++ wget GeoIP-devel git swig make perl perl-ExtUtils-Embed readline-devel zlib-devel -y

echo "===================="
echo "Install openssl"

cd /tmp

command -v wget >/dev/null 2>&1 || { echo >&2 "I require wget but it's not installed.  Aborting."; }
command -v git >/dev/null 2>&1 || { echo >&2 "I require git but it's not installed.  Aborting."; }

if ![ -e /tmp/openssl-1.0.2k.tar.gz ]
then
    wget -c http://www.openssl.org/source/openssl-1.0.2k.tar.gz
fi

if [-d /tmp/openssl-1.0.2k ]
then
    rm -rf /tmp/openssl-1.0.2k
fi
tar -zxvf openssl-1.0.2k.tar.gz

cd openssl-1.0.2k
./config
# default install dir is "/usr/local/ssl"  
make && make install

cd /tmp

echo "======================="
echo "Install pcre-jit"

if ![ -e /tmp/pcre-8.40.tar.gz ]
then
    wget https://ftp.pcre.org/pub/pcre/pcre-8.40.tar.gz
fi

if [-d /tmp/pcre-8.40]
then
    rm -rf /tmp/pcre-8.40
fi
tar -zxvf pcre-8.40.tar.gz
cd pcre-8.40
./configure --enable-jit  
make && make install


echo "======================="
echo "Install OpenWAF"

cd /tmp

if ![ -e /tmp/openresty-1.11.2.2.tar.gz ]
then
    # default install dir is "/usr/local/openresty"
    wget https://openresty.org/download/openresty-1.11.2.2.tar.gz
fi

if [-d /tmp/openresty-1.11.2.2 ]
then
    rm -rf /tmp/openresty-1.11.2.2
fi
tar -zxvf openresty-1.11.2.2.tar.gz

cd /opt

if [ -d /opt/OpenWAF ]
then
    rm -rf /opt/OpenWAF
fi
git clone https://github.com/titansec/OpenWAF.git

mv /opt/OpenWAF/lib/openresty/ngx_openwaf.conf /etc
mv /opt/OpenWAF/lib/openresty/configure /tmp/openresty-1.11.2.2

cp -RP /opt/OpenWAF/lib/openresty/* /tmp/openresty-1.11.2.2/bundle/
cd /opt/OpenWAF

export PWD=/opt/OpenWAF

make install

cd /tmp/openresty-1.11.2.2/

./configure --with-pcre-jit --with-ipv6  --with-http_stub_status_module --with-http_ssl_module --with-http_realip_module --with-http_sub_module --with-http_geoip_module --with-openssl=/tmp/openssl-1.0.2k --with-pcre=/tmp/pcre-8.40
make && make install

cd /tmp

rm -rf openssl-1.0.2k
rm openssl-1.0.2k.tar.gz

rm -rf openresty-1.11.2.2
rm openresty-1.11.2.2.tar.gz

rm -rf pcre-8.40
rm pcre-8.40.tar.gz