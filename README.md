# 谷粒商城

> 尚硅谷 谷粒商城是一套电商项目, 包括前台商城系统以及后台管理系统, 基于 SpringCloud + SpringCloudAlibaba + MyBatis-Plus实现, 前台商城系统包括：用户登录、注册、商品搜索、商品详情、购物车、下订单流程、秒杀活动等模块。后台管理系统包括：系统管理、商品系统、优惠营销、库存系统、订单系统、用户系统、内容管理等七大模块。

![](https://oss.yiki.tech/gmall/202512170319589.png)

## 开发环境

|     软件      |   版本   |
| :-----------: | :------: |
|      JDK      |    8     |
|     Maven     |  3.6.3   |
|     Linux     | CentOS 7 |
|     Node      | 14.21.3  |
|     MySQL     |  8.0.31  |
|     Redis     |  7.0.10  |
|     Nacos     |  2.0.3   |
|   Sentinel    |  1.8.6   |
|    Zipkin     |  2.20.2  |
|     Nginx     |  1.16.1  |
|    Docker     |  26.1.4  |
|   RabbitMQ    |  3.12.0  |
| Elasticsearch |  7.6.2   |
|    Kibana     |  7.6.2   |

> 由于我的 Mac 内存较小，我选择使用两台硬件设备：Mac: m1 + 16G 和 Windows: 7800x3d + 32G。Mac 作为主力开发机器，Windows 用于启动后台管理前端项目和虚拟机。
>
> 在虚拟机中，我安装了微服务所需的环境，包括数据库（MySQL、Redis）、中间件（Nginx、RabbitMQ、Elasticsearch等）和阿里云云原生组件（Zipkin、Nacos、Sentinel等）。这样可以方便地进行微服务开发和测试，并且不会占用Mac的内存资源。
> 
> 为了确保所有组件能够顺畅通信，我将它们都配置在 `192.168.0` 网段中。这样，它们之间的通信就可以高效地进行，并且可以确保整个开发过程的顺利进行。

![](https://oss.yiki.tech/gmall/202512170319487.png)

![](https://oss.yiki.tech/gmall/202512170322773.png)

## 项目分类

![](https://oss.yiki.tech/gmall/202512170322401.png)

## 电子商务

![](https://oss.yiki.tech/gmall/202512170322593.png)