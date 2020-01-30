# Simple-Framework使用文档

​		Simple-Framework是一种轻量级Java Web框架，可基于此框架进行简单的web开发。

​		本项目主要供学习交流使用，Email: xctian@zju.edu.cn

## 简介

​		Simple-Framework是基于原生Servlet实现的一种轻量级Java Web框架，它具有目前市面上流行的Java Web框架(如Spring、SpringMVC)所具备的核心功能：**Bean容器，依赖注入，请求转发控制器，IOC和AOP功能，事务管理控制**等。Simple-Framework的实现过程并未引入或使用其他web框架，它是一种轻量级MVC框架的自行开发和探索，同时也是本人对Java Web后台开发技术知识的学习及整合。

​		实现过程中使用到的技术有：**Tomcat、Servlet 、JDBC、数据库、反射、CGLIB动态代理、设计模式**(模板模式、责任链模式)、**ThreadLocal**等。虽然Simple-Framework功能没有流行框架那样齐全，但其实现和使用非常轻量级，适合作为学习Java Web框架实现的参考项目。以下介绍Simple-Framework的功能组成和特性：

### 配置文件

- 配置文件少，只需在对应web项目resources目录下创建并配置simple.properties的文件
- 配置项简单，只需配置数据库连接相关参数、项目基础包名，以及JSP和静态资源文件基础路径
- 部分配置项支持缺省配置

### **类加载和操作工具**

- 本项目提供一个自定义类加载工具类ClassUtil，提供类操作相关的方法，如获取类加载器、加载指定类、获取指定包名下的所有类等
- 提供ClassHelper助手类封装ClassUtil，包含以下功能：获取应用包名下所有类，应用包名下所有Service类，应用包名下所有Controller类，获取应用包名下所有Bean类

### 注解

​	本项目提供以下自定义注解

- Controller：控制器类标记注解，在控制器类上使用，用以标注控制器类
- Service：服务类标记注解，在服务类上使用，用以标注服务类
- Inject：依赖注入标记注解，在成员变量上使用，用以表示将服务类依赖注入进来
- Action：单成员注解，在控制器类中的方法上使用，用以表明该方法是处理对应请求的Action方法，含义String类型的内部成员value，用以接收请求类型与路径

### Bean容器

- 提供反射工具类ReflectionUtil，封装Java反射相关API，用以提供获取实例化对象的相关方法
- Bean容器对象BEAN_MAP的类型：Map<Class<?>，Object>，key：类的Class对象，value：Class对象对应的Bean实例
- 提供BeanHelper助手类，封装BEAN_MAP，BeanHelper加载时将BEAN_MAP初始化，项目运行过程中可通过BeanHelper.getBean(Class<T> cls)方法直接获取对应Bean实例

### 依赖注入和IOC

- 提供IocHelper助手类，IocHelper初始化时会扫描所有Inject注解，并从Bean容器中找到对应的Service实例通过ReflectionUtil对成员变量进行注入（详见文末提供的开发文档）
- 本项目通过Bean容器及依赖注入功能实现IOC(控制反转)

### Controller加载

- 对于Action方法的请求参数，封装请求对象Request，包含请求方法requestMethod和请求路径requestPath
- 为每一个Request封装对应Handler，Handler包含处理请求的Controller类controllerClass和对应方法actionMethod

- 提供一个ControllerHelper控制器助手类，封装一个用于存放Request和对应Handler的Map容器Map<Request,Handler> ACTION_MAP，ControllerHelper初始化时会自动初始化ACTION_MAP（实现参考文末开发文档）

### 请求分发器DispatcerServlet

- 添加请求参数封装对象Param，封装一个paramMap用于接收请求参数

- DispatcerServlet继承HttpServlet，在重写的init方法进行初始化时会将以上Helper进行初始化，
- DispatcerServlet重写的Service中主要是获取HttpServletRequst中的内容，将必要的参数封装Param对象，通过ControllerHelper拿到对应请求处理的Handler，传入Param利用反射对Action方法发起调用
- 对应Action方法返回值提供两种处理情况：(1)返回值是View类型视图对象，则返回一个JSP; (2)返回值是Data类型数据对象，则返回JSON数据

### AOP和事务管理

​		本项目提供的AOP框架基于注解

- 提供切面注解Aspect，它是单成员注解，内部有一个注解类型的成员，内部注解用于标明被拦截的类
- 提供一个抽象切面类AspectProxy，只需根据业务需求定义自己的切面类，扩展AspectProxy并定义Aspect注解，然后完成特定的钩子方法，即可将横切逻辑与业务逻辑分离
- 通过责任链模式，实现对一个切点织入多个增强
- 框架启动时，会自动将Class对象及其经过AOP处理后的实例对象以k,v的形式存入IOC容器BEAN_MAP
- 提供方法级别的事务注解Transaction，用于对Service类中的方法进行事务管理

## 使用说明

​		本项目在提供了一个简单的web项目managementsystem，里面整合并使用了Simple-Framework进行开发，该项目用于对Simple-Framework进行简单的测试和使用说明，也可以自行选取web项目进行整合使用。本项目作为web框架并未提供ORM框架功能，与数据库交互工作需要自行完成。以下是Simple-Framework使用说明：

### 项目导入

- clone本项目到本地，使用IDEA打开，配置maven和本地仓库，等待依赖自动导入完成。
- 该项目是一个Web框架，它可以作为jar包被其他项目所依赖使用。使用maven的install命令，将项目打包到本地仓库。
- 打开本地的web项目，pom文件中引入以下的dependency：

```xml
<dependency>
    <groupId>com.xctian</groupId>
    <artifactId>simple-framework</artifactId>
    <version>1.0-SNAPSHOT</version>
<dependency>
```

### 配置项说明

在web项目resources目录下创建并配置simple.properties的文件，配置示例如下：

```properties
simple.framework.jdbc.driver=com.mysql.jdbc.Driver
simple.framework.jdbc.url=jdbc:mysql://localhost:3306/demo
simple.framework.jdbc.username=root
simple.framework.jdbc.password=admin

simple.framework.app.base_package=org.simple4j.managementsystem
simple.framework.app.jsp_path=/WEB-INF/view/
simple.framework.app.asset_path=/asset/
```

其中前4项是jdbc相关配置，simple.framework.app.base_package是web应用基础包名，必须进行配置。simple.framework.app.jsp_path是JSP文件基础路径，缺省值为/WEB-INF/view/，simple.framework.app.asset_path为静态资源文件基础路径缺省值为/asset/

### 注解使用

- Controller和Service是类级别的标记注解，只能使用在类上

- Inject是依赖注入注解，在Controller类中的成员变量上使用

- Transaction是方法级别的注解，只能用于Service类中的方法上

- Action是方法级别的注解，在Controller类中的方法上使用，用以表明该方法是处理对应请求的Action方法，在Action需要配置请求方法和路径：

  ```java
  @Controller
  public class XXXController {
      @Inject
      private CustomerService customerService;
      ...
      @Action("get:/customer")
      public View index() {
          List<Customer> customerList = customerService.getCustomerList();
          LOGGER.info("列表展示");
          return new View("customer.jsp").addModel("customerList", customerList);
      }
      ...
  }
  ```

- Aspect是类级别的注解，用于自定义切面类：

  ```java
  @Aspect(Controller.class)
  public class ControllerAspect extends AspectProxy {
      private long begin;
  
      @Override
      public void before(Class<?> cls, Method method, Object[] params) throws Throwable 	  {
      	...    
      }
  
      @Override
      public void after(Class<?> cls, Method method, Object[] params, Object result) throws Throwable {
          ...
      }
  }
  ```

  如在切面类ControllerAspect类上使用注解@Aspect(Controller.class)则表明所有被Controller注解的类都会被拦截，其所有方法会被织入增强

### AOP说明

根据业务需求定义自己的切面类，扩展AspectProxy并定义Aspect注解，然后完成特定的钩子方法，示例：

```java
@Aspect(Controller.class)
public class ControllerAspect extends AspectProxy {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerAspect.class);
    
    private long begin;

    @Override
    public void before(Class<?> cls, Method method, Object[] params) throws Throwable {
        LOGGER.debug("---------- begin ----------");
        LOGGER.debug(String.format("class: %s", cls.getName()));
        LOGGER.debug(String.format("method: %s", method.getName()));
        begin = System.currentTimeMillis();
    }

    @Override
    public void after(Class<?> cls, Method method, Object[] params, Object result) throws Throwable {
        LOGGER.debug(String.format("time: %dms", System.currentTimeMillis() - begin));
        LOGGER.debug("----------- end -----------");
    }
}
```

AspectProxy抽象类中提供了一系列横切逻辑方法以供重写，具体可以直接进入源码查看

## Simple-Framework开发说明

[这里](note/note.md)教你如何从零开发Soft-RPC框架，介绍本项目各个功能模块的实现要点，开发思路，以及一些学习笔记。