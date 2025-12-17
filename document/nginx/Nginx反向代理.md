# Nginx 反向代理

> Nginx 反向代理（Reverse Proxy）是一种常见的应用程序设计模式，它通过将客户端的请求转发给内部服务器来实现客户端与服务器的通信。与传统的代理服务器不同，反向代理服务器是针对服务器端而言的，而不是客户端。

## 使用反向代理 解决 端口号问题

> 通常情况下，一个域名只能绑定一个IP地址和一个端口号。如果有多个服务需要通过同一个域名来访问，那么就需要使用反向代理来实现。
>
> 我们希望直接通过域名去访问服务资源. `http://xxx.xxx.xxx` 默认都是 80 端口, 而一个服务器 80 端口 只有一个。
>
> 将来我们希望多个工程可以使用 域名去访问. 此时就需要借助于 nginx 的反向代理. 所有请求通过nginx 转发到对应的服务, 在通过网关路由到具体的服务

![](https://oss.yiki.tech/gmall/202512180614624.png)

### 修改 Nginx 配置

```shell
vim /usr/local/nginx/conf/nginx.conf
```

```shell
worker_processes  1;

events {
    worker_connections  1024;
}


http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        on;
    keepalive_timeout  65;

    # 代理网关
    server {
        listen       80;            # listen 指定了监听的端口
        server_name  api.gmall.com; # server_name 指定了域名或服务器地址。

        location / { # location / 指定了所有的请求路径都会被转发到后面的 proxy_pass 配置上。
            proxy_pass http://192.168.0.111:8888; # proxy_pass 配置指定了反向代理的地址，由服务器的 IP 地址和端口号组成。
        }
    }

    # 代理后台管理
    server {
        listen       80; # 监听 80 端口
        server_name  manager.gmall.com; # 服务器地址或绑定的域名

        location / { # 访问 80 端口后所有的路径都转发到 proxy_pass 配置中的地址
            proxy_pass http://192.168.0.121:1000; # 配置反向代理的 Ip 地址和端口号 [注: url 地址需要添加 "http://"]
        }
    }

}
```

![](https://oss.yiki.tech/gmall/202512180616681.png)

```shell
# 进入 nginx 目录
cd /usr/local/nginx/sbin

# 重新加载
./nginx  -s  reload
```



### Mac 修改 hosts

```shell
sudo vim /etc/hosts
```

![](https://oss.yiki.tech/gmall/202512180617824.png)

```shell
192.168.0.101 api.gmall.com manager.gmall.com
```

![](https://oss.yiki.tech/gmall/202512180617600.png)

### 测试

![](https://oss.yiki.tech/gmall/202512180618195.png)

![](https://oss.yiki.tech/gmall/202512180620434.png)

## Nginx 加入后请求流程分析

![](https://oss.yiki.tech/gmall/202512180640065.png)

![](https://oss.yiki.tech/gmall/202512180644659.png)

> 浏览器输入域名-> 本机hosts 文件对此域名进行解析 -> 发送真实请求 ip + 请求路径 -> 根据 ip + 80 端口找到 nginx 服务器 -> nginx 配置了反向代理根据 域名头信息找到 当前 server -> 根据反向代理后的  ip 地址找到网关应用 -> 网关根据 断言配置路由到对应的 微服务 -> 找到对应的 controller 方法处理 -> 处理完后沿路返回到用户浏览器

#### 第一阶段：地址解析（Client Side）

1. **域名输入**：在浏览器地址栏输入 `http://api.gmall.com/pms/brand`。
2. **本地劫持**：操作系统检查 `hosts` 文件，发现配置：`192.168.0.101 api.gmall.com`。
3. **封包发送**：浏览器不再请求 DNS 服务器，直接向 `192.168.0.101` 的 **80 端口** 发起 TCP 连接，并在 HTTP 请求头中携带 `Host: api.gmall.com`。

#### 第二阶段：反向代理（Nginx 层）

1. **监听匹配**：Nginx 监听到 80 端口请求，提取请求头中的 `Host` 字段。
2. **配置定位**：匹配到 `server { server_name api.gmall.com; }` 配置块。
3. **请求转发**：执行 `proxy_pass`。
   - **目的地**：请求被转发至内网网关地址，例如 `http://192.168.0.111:8888`。

#### 第三阶段：网关分发（Gateway 层）

1. **断言匹配（Predicate）**：网关接收请求，扫描 `routes` 配置：

   ```yml
         routes: # 路由
           - id: pms-route # 路由唯一标识, 商品管理路由
             uri: lb://pms-service # 路由到哪里, 路由到服务名对应的服务
             predicates: # 断言
               - Path=/pms/** # 什么样的请求到此路由 进而进入路由该服务. pms 开头的请求 都 路由到 pms-service 服务
   ```

2. **服务发现**：识别到路径匹配 `/pms/**`，网关通过注册中心（如 Nacos/Eureka）拉取 `gmall-pms` 服务的所有实例 IP。

3. **负载均衡**：通过 Ribbon 或 LoadBalancer 选出一个具体的微服务实例。

#### 第四阶段：业务处理（Service 层）

1. **控制器映射**：请求到达 `gmall-pms` 服务，Spring MVC 的 `DispatcherServlet` 根据 URL `/pms/brand` 找到对应的 `BrandController`。
2. **逻辑执行**：执行方法，访问数据库，完成业务逻辑。
3. **原路返回**：数据按 `Controller -> Gateway -> Nginx -> Browser` 路径依次封装返回，最终用户看到 JSON 数据或页面。

![](https://oss.yiki.tech/gmall/202512180634230.png)