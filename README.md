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

#### 设置API项目回源时额外携带参数
当用户请求到api项目中，api验证通过放行到后端服务器时，这个从api发起到后端服务器的请求可以额外携带参数，携带的参数可以以json的方式放到 /mnt/key/params.config 文件中。可以执行一下命令将这个文件直接下载下来，在这个基础上更改：
````
cd /mnt/key/
wget http://down.zvo.cn/key/params.json -O params.config
````
这里面的是json格式。
代理像后端发送请求时，会额外追加参数，这个追加的参数如果跟用户本身请求携带参数的名字一致时，会覆盖本身请求的参数，以这个单独传入设置的为准
这个只有用户发起post请求、且请求方式是formdata方式时才会有效，其他方式比如payload方式发送消息体、以及get方式请求，并不会携带 params.config 中自定义设置的参数。

## 说明
api服务在使用时，正常响应，会在headers响应头中，携带 count、use_count 两个参数的返回，分别代表这个key的总次数、已使用次数
