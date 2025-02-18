用于管理你的API接口，可以开通多个key对外分发，每个key设置一个允许使用次数（请求次数） ，次数用完后这个key将无法再使用。  
只有正常http 200响应的才会计次数
## 流程原理
你本身提供API的系统无需任何改动，单独部署一层本应用，用户请求时先经过本应用进行验证key、次数 等是否合格，合格才会放行到你原有的API接口， 当API接口处理完响应时，先将响应回执到本应用，本应用会判定如果是http 200 的响应，则认为是API响应成功，则这个key的使用次数会加1，否则使用次数不会增加（后端API如果未处理成功，key的已使用次数不会增加），然后将回执结果再响应给用户。

## 应用架构
应用分为两个端：
* admin 端是一个可视化管理后台，可以开通key、对key进行管理、设置key的可用次数、设置key允许使用的接口等，它是给自己管理人员使用，用于进行管理的。
* api 端是用于对外开放接口使用，用于请求的，也就是实际key的校验拦截及计数等。

实际使用时，如果你admin开完一堆的key后，不需要对key进行管理了，你完全可以吧 admin 端直接结束掉。这样能更好的节省服务器资源、提高安全  
实际使用时，api 端 跟 admin 端是使用了两个不同的端口， api端默认使用80端口， admin端默认使用82端口。  

## config.properties 配置文件
配置文件位于 /mnt/key/config.properties  
其中的配置解释:  

````
# 实际提供API服务的后端域名，注意格式是  http://12.12.12.12  这样的格式，严格按照这个格式来。另外尽量用 http 协议。 比如你实际提供后端服务的API接口是  http://12.12.12.12/a/b.json ，那么这里填写 http://12.12.12.12
api.domain=http://192.168.31.100

# redis服务的host，填写格式如 127.0.0.1 ，默认不设置便是 127.0.0.1
redis.host=127.0.0.1
# redis服务的端口，填写格式如 6379 ，默认不设置便是 6379
redis.port=6379
# redis服务的密码 ，默认不设置便是无密码
redis.password=

# 项目运行产生的日志文件存放到哪个目录下。 注意这个目录要提前存在，创建好。 一般默认即可，不需要改动，安装都是安装到 /mnt/key/ 目录下的
log.path=/mnt/key/logs/
````

其中 api. 开头的是服务于 api 端的，  admin.开头的是服务于 admin 端的。  
如果不是这两者开头的，那就是两端共用的

## 私有部署
#### 准备工作
服务器规格：1核1G、10G系统盘 、x86_64架构（Intel的CPU）
操作系统： centos7.4、7.6
#### 部署脚本

````
yum -y install wget && wget https://raw.githubusercontent.com/xnx3/ApiKey/refs/heads/main/else/install_apikey_api.sh -O ~/install.sh && chmod -R 777 ~/install.sh && sh ~/install.sh
````
注意， /mnt/key/config.properties 中的 api.domain 要设置上实际API接口所在的域名

## 使用说明
#### admin 端使用
访问 127.0.0.1:82 使用 账号 admin  密码 admin 进行登录使用。  
可以添加一个key进行测试。  
#### api 端使用
API端是配合admin开通的key，给用户进行请求使用的  
api服务在使用时，需要额外通过get或post额外携带一个key参数，接口如果正常响应（你的后端API响应码是200），会在headers响应头中，携带 count、use_count 两个参数的返回，分别代表这个key的总次数、已使用次数
