
# 项目目录的介绍
## admin/src/main/java/com.moreo.shorlink.admin
1. common    公共包
   biz.user   biz: 业务，user: 用户
   constant   系统常量类
   convention   规约
   database    数据库持久层
   enums    系统枚举
   serialize    脱敏序列化类，比如将一些敏感数据(手机号，身份证号)脱敏后传给前端
   web    全局异常拦截器

2. config   配置类
3. controller   控制层，用来接收请求
4. dao    数据持久层
   entity    数据库表实体
   mapper    持久层框架
5. dto    数据传输实体类, 前后端交互
   req   数据请求实体
   resp    数据返回实体
6. remote.dto     RPC 远程调用
7. service    业务接口层
   impl   业务实现层
8. toolkit     工具类

上课写的内容基本在语雀文档里面有，这里记录我想写的，一些规范、功能等

# Java 内容
1. 在根项目中的`pom.xml`中统一配置整个项目的依赖包的版本，在内部模块中的`pom.xml`直接引用包名就可以了
2. 统一定义响应的错误码，并区分不同类型。在大型公司中可能会有一个错误码系统，里面存储了所有的错误码，并且可以申请新的错误码
3. 对不同的端（用户、服务、远程）定义对应的异常，使用全局异常拦截器`web/GlobalExceptionHandler` 来拦截所有`Controller或Service`中抛出的 指定的异常，进行处理
4. 用户敏感信息的脱敏，使用`JsonSerializer`定义反序列化类，再使用注释`@JsonSerialize`在要脱敏的实体类的属性
5. 新增数据时，时间等字段自动填充，使用 MyBatis-Plus 原数据自动填充类（`/config/MyBetaObjectHandler`），再在需要填充的DTO的字段前加注释`@TableField(fill = FieldFill.INSERT_UPDATE)`
6. 使用分布式锁，来防止在短时间内多个同一用户名的注册。因为规定用户名不能重复，所以多个用户如果在同一时刻使用同样的 未被使用的 用户名注册，需要加锁，来只让一个用户注册成功
7. 如果多个用户在同一时刻，使用不同的用户名，造成大量请求，可以使用限流的方式来预防
8. 分库分表，使用 ShardingSphere，更改数据连接的配置，可以直接使用
9. 加密存储，在上一条的配置中增加加密的配置
10. 用户上下文封装，在缓存中存储当前登陆用户的信息，在其他请求时，可以直接判断这个请求的用户是否已经登陆，并直接从缓存中拿取用户信息
11. 

