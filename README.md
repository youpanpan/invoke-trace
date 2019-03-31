#  Controller方法调用跟踪jar
   通过使用自定义注解处理类的方式，实现在编译阶段生成指定的classes文件，该classes的作用就是拦截配置的方法，记录下方法调用前后的方法及时间信息，并提供接口展示这部分信息。  主要的作用就是为了帮助项目组新成员熟悉项目的接口及调用过程，从整体结构上了解整体框架。
## 项目组成结构
+ screenshots：截图
+ union-commons-util：工具类项目
+ union-invoke-trace：方法跟踪核心项目
+ union-invoke-trace-test：方法跟踪测试项目
## 项目技术
+ 后端技术
   + SpringBoot    
   + Thymeleaf
   + AbstractProcessor（自定义注解处理）
   + Mysql
+ 前端技术
   + Jquery
   + Bootstrap
   + layui
## 项目运行

1.在union-commons-util根目录下执行  
`mvn clean install`  

2.编译打包项目，在根目录下执行
`mvn clean install`

3.在union-invoke-trace根目录下执行  
`mvn clean install`  

4.在union-invoke-trace-test根目录下执行  
`mvn clean package`

5.执行生成的union-invoke-trace-test的jar
`java -jar union-invoke-trace-test-1.0-SNAPSHOT`

6.访问[http://localhost:20010](http://localhost:20010)  

## 使用说明
1.将union-invoke-trace打成jar，然后在项目中引入该jar
2.在SpringBoot启动类中配置
`@EnableInvokeTrace("execution(* com.chengxuunion.test.demo..*(..))")`
这里配置的是pointcut表达式

## 提供的接口
接口 | 描述
-|-
/invoke/method | 展示页面
/invoke/method/list-page | 查询所有的方法调用栈
/invoke/method/get?fullName=方法全名 | 获取指定方法的调用栈
/invoke/method/statistics?fullName=方法全名&number=20 | 获取指定方法近number次调用时间

## 配置项
除了默认的内存存储调用信息外，还可以在src/main/resources下新建union-invoke-trace.properties文件配置·存储方式
```
# 存储方式
storage.class=com.chengxuunion.invoketrace.storage.impl.MemoryInvokeTraceStorage
#storage.class=com.chengxuunion.invoketrace.storage.impl.DataBaseInvokeTraceStorage

# 数据库配置, 在存储方式为数据库的时候有效
url=jdbc:mysql://localhost:3306/invoke_trace?useUnicode=true&amp;characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true
username=root
password=123456
type=com.alibaba.druid.pool.DruidDataSource
driver-class-name=com.mysql.jdbc.Driver
filters=stat
maxActive=200
initialSize=1
maxWait=60000
minIdle=1
timeBetweenEvictionRunsMillis=60000
minEvictableIdleTimeMillis=300000
validationQuery=select 'x'
testWhileIdle=true
testOnBorrow=false
testOnReturn=false
poolPreparedStatements=true
maxOpenPreparedStatements=20
```

数据库的初始化文件在union-invoke-trace下的文档/数据库下


## 展示
1.方法调用栈

![方法调用栈](https://github.com/youpanpan/invoke-trace/blob/master/screenshots/%E6%96%B9%E6%B3%95%E8%B0%83%E7%94%A8%E6%A0%88.png?raw=true)

2.方法详情

![方法详情](https://github.com/youpanpan/invoke-trace/blob/master/screenshots/%E6%96%B9%E6%B3%95%E8%AF%A6%E6%83%85.png?raw=true)

3.最近20次统计调用时间统计

![最近20次统计调用时间](https://github.com/youpanpan/invoke-trace/blob/master/screenshots/%E6%9C%80%E8%BF%9120%E6%AC%A1%E7%BB%9F%E8%AE%A1.png?raw=true)
