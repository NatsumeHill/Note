# Nginx配置

OpenWAF对代理进行了封装，使用twaf_access_rule对代理进行配置。

twaf_access_rule 涉及 ssl_certificate_by_lua ， rewrite_by_lua 和 balancer_by_lua 三个阶段：
```
{
    "twaf_access_rule": [
        "rules": [                                 -- 注意先后顺序
            {                                      
                "ngx_ssl": false,                  -- nginx 认证的开关
                "ngx_ssl_cert": "path",            -- nginx 认证所需 PEM 证书地址
                "ngx_ssl_key": "path",             -- nginx 认证所需 PEM 私钥地址
                "host": "www.baidu.com",           -- 域名，正则匹配
                "path": "/",                       -- 路径，正则匹配
                "port": 80,                        -- 端口，默认 80
                "server_ssl": false,               -- 后端服务器 ssl 开关
                "forward": "server_5",             -- 后端服务器 upstream 名称
                "forward_addr": "1.1.1.2",         -- 后端服务器ip地址
                "forward_port": "8080",            -- 后端服务器端口号（缺省80）
                "uuid": "access_567b067ff2060",    -- 用来标记此规则的 uuid，api 中会用到，要保证全局唯一
                "policy": "policy_uuid"            -- 安全策略 ID
            }
        ]
}
```

### ssl_certificate_by_lua

ssl_certificate_by_lua 阶段用于 ssl 认证，涉及到 access_rule 配置的有 ngx_ssl，ngx_ssl_cert 和 ngx_ssl_key

这部分配置可以节省 nginx 中 ssl 配置的重复性，如：

```nginx
    server {
        listen 443 ssl;
        server_name www.abc.com;
        
        ssl_certificate /opt/OpenWAF/conf/ssl/abc.crt;
        ssl_certificate_key /opt/OpenWAF/conf/ssl/abc.key;
        ssl_protocols TLSv1.1 TLSv1.2;

        location / {
            ...
        }
    }
    
    server {
        listen 443 ssl;
        server_name www.xyz.com;
        
        ssl_certificate /opt/OpenWAF/conf/ssl/xyz.crt;
        ssl_certificate_key /opt/OpenWAF/conf/ssl/xyz.key;
        ssl_protocols TLSv1.1 TLSv1.2;

        location / {
            ...
        }
    }
    
    ...
```

原始 nginx 配置如上，那么加上 WAF 防护，且经过 access_rule 的优化后，可写为：

```nginx
    server {
        listen 443 ssl;
        server_name _;
        
        ssl_certificate /opt/OpenWAF/conf/ssl/nginx.crt;
        ssl_certificate_key /opt/OpenWAF/conf/ssl/nginx.key;
        ssl_protocols TLSv1.1 TLSv1.2;
        
        include                     /opt/OpenWAF/conf/twaf_server.conf;  #添加 WAF 防护
        ssl_certificate_by_lua_file /opt/OpenWAF/app/twaf_ssl_cert.lua;  #动态指定 SSL 证书

        location / {
            ...
        }
    }
```

此时只需在 access_rule 中指定 SSL 证书即可，如：

```
{
    "twaf_access_rule": [
        "rules": [
            {                                      
                "ngx_ssl": true,
                "ngx_ssl_cert": "opt/OpenWAF/conf/ssl/abc.crt",
                "ngx_ssl_key":  "/opt/OpenWAF/conf/ssl/abc.key",
                "host": "www.abc.com",
                "path": "/",
                "port": 443,
                ...
            },
            {                                      
                "ngx_ssl": true,
                "ngx_ssl_cert": "opt/OpenWAF/conf/ssl/xyz.crt",
                "ngx_ssl_key":  "/opt/OpenWAF/conf/ssl/xyz.key",
                "host": "www.xyz.com",
                "path": "/",
                "port": 443,
                ...
            }
        ]
    }
}
```

如此，多个 ssl 站点，也可用 access_rule 实现动态分配 SSL 证书，不需变更 nginx 配置

### rewrite_by_lua

rewrite_by_lua 阶段，会依据请求头中的 host，port，uri 等信息，确认后端服务器地址及选用的策略

下面详细讨论 nginx 配置是如何转到 access_rule 中配置的

```nginx

    upstream aaa {
        server 1.1.1.1;
    }
    
    server {
        listen       80;
        server_name  www.aaa.com;

        location / {
            proxy_pass http://aaa;
        }
    }
```

上面 nginx 配置，加上 OpenWAF 防御后，对应 nginx 配置如下：

```nginx
    upstream test {
       server 0.0.0.1; #just an invalid address as a place holder
       balancer_by_lua_file /opt/OpenWAF/app/twaf_balancer.lua;
    }
    
    server {
        listen       80;
        server_name  _;
        include      /opt/OpenWAF/conf/twaf_server.conf;

        location / {
            proxy_pass $twaf_upstream_server;
        }
    }
```

对应 access_rule 配置如下：

```
{
    "twaf_access_rule": [
        "rules": [
            {
                "host": "www.aaa.com",
                "path": "/",
                "port": 80,
                "forward": "test",
                "forward_addr": "1.1.1.1",
                "forward_port": 80
                ...
            }
        ]
    }
}
```

其中 forward 是为 nginx 配置中的 $twaf_upstream_server 变量赋值  
forward_addr 和 forward_port 只在 upstream 中使用 balancer_by_lua 才会生效，否则不需配置这两个值  

前面 ssl_certificate_by_lua 的配置，节省了因 ssl 证书配置使得一个 ssl 站点对应一个 nginx 的 server 配置的重复性

这部分 rewrite_by_lua 的配置同样可以节省 nginx 中配置的重复性，如：

```nginx

    upstream aaa_1 {
        server 1.1.1.1;
    }
    
    upstream_aaa_2 {
        server 1.1.1.2;
    }
    
    upstream bbb {
        server 2.2.2.2:8000;
    }
    
    server {
        listen       80;
        server_name  www.aaa.com;

        location / {
            proxy_pass http://aaa_1;
        }
        
        location /a {
            proxy_pass http://aaa_2;
        }
    }
    
    server {
        listen       90;
        server_name  www.bbb.com;

        location / {
            proxy_pass http://bbb;
        }
    }
    
    ...
```

上面 nginx 配置，加上 OpenWAF 防御后，对应 nginx 配置如下：

```nginx
    upstream test {
       server 0.0.0.1; #just an invalid address as a place holder
       balancer_by_lua_file /opt/OpenWAF/app/twaf_balancer.lua;
    }
    
    server {
        listen       80;
        listen       90;
        server_name  _;
        include      /opt/OpenWAF/conf/twaf_server.conf;

        location / {
            proxy_pass $twaf_upstream_server;
        }
    }
```

对应 access_rule 配置如下：

```
{
    "twaf_access_rule": [
        "rules": [
            {
                "host": "www.aaa.com",
                "path": "/a",
                "port": 80,
                "forward": "test",
                "forward_addr": "1.1.1.2",
                "forward_port": 80
                ...
            },
            {
                "host": "www.aaa.com",
                "path": "/",
                "port": 80,
                "forward": "test",
                "forward_addr": "1.1.1.1",
                "forward_port": 80
                ...
            },
            {
                "host": "www.bbb.com",
                "path": "/",
                "port": 90,
                "forward": "test",
                "forward_addr": "2.2.2.2",
                "forward_port": 8000
                ...
            }
        ]
    }
}
```

从以上配置可以看出，access_rule 节省了因域名，监听端口，路径，upstream 等因素造成的配置复杂性

而且，以后可通过 api，动态添加接入规则，不需中断业务，而修改 nginx 配置，可能会中断业务

注意：在上例中，www.aaa.com 站点下，有 '/' 和 '/a' 两个路径，access_rule 是数组，因此，要将有关 '/a' 的配置放在 '/' 前

本地资源配置:

```nginx
    upstream test {
       server 0.0.0.1; #just an invalid address as a place holder
       balancer_by_lua_file /opt/OpenWAF/app/twaf_balancer.lua;
    }
    
    server {
        listen       80;
        server_name  www.aaa.com;
        include      /opt/OpenWAF/conf/twaf_server.conf;

        location / {
            proxy_pass $twaf_upstream_server;
        }
        
        location /a {      #本地资源
            root /xxx;
            index xxx;
        }
    }
```

对应 access_rule 配置如下:

```
{
    "twaf_access_rule": [
        "rules": [
            {
                "host": "www.aaa.com",
                "path": "/",
                "port": 80,
                "forward": "test",
                "forward_addr": "1.1.1.1",
                "forward_port": 80
                ...
            }
        ]
    }
}
```

这里可以看到，仅仅是配置了根目录的接入规则，并不需单独为 '/a' 进行配置  

因为访问 www.aaa.com/a 目录下资源，已经匹配中了这条接入规则，但对应的 nginx 配置中并没有 proxy_pass，  
因此 forward ，forward_addr 和 forward_port 三个参数并不会生效

当然如果你很任性，非要添加有关 '/a' 目录的接入规则，则配置如下：

```
{
    "twaf_access_rule": [
        "rules": [
            {
                "host": "www.aaa.com",
                "path": "/a",
                "port": 80,
                ...
            },
            {
                "host": "www.aaa.com",
                "path": "/",
                "port": 80,
                "forward": "test",
                "forward_addr": "1.1.1.1",
                "forward_port": 80
                ...
            }
        ]
    }
}
```

从上面配置看出，因为 forward ，forward_addr 和 forward_port 三个参数并不会生效，所以无需配置

access_rule 中还剩最后两个参数，uuid 和 policy  
uuid:   用来标记接入规则的 uuid，api 中会用到，要保证全局唯一  
policy: 指定策略名称，OpenWAF 自带策略有 twaf_default_conf 和 twaf_policy_conf，若不配置 policy，缺省使用 twaf_default_conf 策略