## Simple-Framework开发文档

### Bean容器的实现

**实现自定义的类加载工具类：**首先我们会在配置文件里去配置web应用的基础包名，它相当于我们web应用代码存放的一个基础路径。框架初始化时，ClassUtil会利用反射去加载基础包名下的所有.class文件，主要是利用class.forName方法（详情见ClassUtil），并存放到一个BEAN_SET集合里面。IOC容器的实现主要是依赖于一个Map对象，他的key是class对象，value是class对象对应的实例，在项目里面默认是单例的。

**实现Bean容器：**完成了上述Set集合的初始化后，遍历整个Set集合，对于每一个class对象通过反射的方法实例化一个instance，然后将class对象和instance以key value的形式存入整个bean Map中，这样的话在框架最开始初始化的时候，map里面就已经初始化好了基础包名底下的所有class的实例对象

```java
  // 初始化，将所有实例化的Bean对象缓存至BEAN_MAP，实现创建好对象放入Bean容器，故可保证单例
    static {
        Set<Class<?>> beanClassSet = ClassHelper.getBeanClassSet();
        for (Class<?> cls : beanClassSet) {
            Object obj = ReflectionUtil.newInstance(cls);
            BEAN_MAP.put(cls, obj);
        }
    }
```

- 关于加载基础包名下所有class文件的实现思路：将包名中的'.'用'/'替换后，获取包名路径下的url列表，遍历所有url，通过url.getProtocol方法判断url头部，从而判断是jar文件还是file文件，如果是file的话递归调用加载所有的class文件，如果是jar的话使用jarURLConnection类去读取jar，遍历jar里的文件加载class结尾的类。

### IOC的实现（依赖注入）

IOC的实现主要依赖于自定义的注解Inject,他是成员变量级别的注解，功能类似于Spring里面的@Autowired

**实现原理：**框架启动的时候，先去遍历上述Bean容器Bean Map，对于每一个bean而言，我们通过反射的方法getDeclaredFields获得他的所有成员变量,然后变量这些成员变量，判断他们是否带Inject注解（通过isAnnotationPresent方法)，如果带Inject注解的话，我们直接从Bean map里面拿到已经实例化好的bean通过反射方法(filed.set)注入到这个实例对象里面。

```java
public class IocHelper {
    // IocHelperer被加载时，就会自动加载static块
    static {
        // 获取Bean Map
        Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();
        if (CollectionUtil.isNotEmpty(beanMap)) {
            //遍历BeanMap
            for (Map.Entry<Class<?>, Object> beanEntry : beanMap.entrySet()) {
                Class<?> beanClass = beanEntry.getKey();
                Object beanInstance = beanEntry.getValue();
                // 获取Bean Class中的所有成员变量,即Bean field
                Field[] beanFields = beanClass.getDeclaredFields();
                if (ArrayUtil.isNotEmpty(beanFields)) {
                    //遍历bean fields
                    for (Field field : beanFields) {
                        //判断当前field是否有Inject注解
                        if (field.isAnnotationPresent(Inject.class)) {
                            //在BeanMap中获取field对应的实例
                            Class<?> beanFieldClass = field.getType();
                            Object beanFiledInstance = beanMap.get(beanFieldClass);
                            if (beanFiledInstance != null) {
                                //通过反射对Bean field进行初始化
                                ReflectionUtil.setField(beanInstance, field, beanFiledInstance);
                            }
                        }
                    }
                }
            }
        }
    }
}
```

### Controller的实现

Controller的实现也是通过自定义注解实现的，主要通过在类上添加@Controller注解表示这是个Controller类，在方法上添加@Action注解表明这是个Action方法，在@Action注解上需要配置请求方法和请求路径，这有点类似于Spring的Controller+RequestMapping。

**实现原理：**自定义一个注解类Controller，他是类级别的注解。再定义一个注解Action，他是方法级别的注解，有一个value值用于配置请求方法和路径。

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {
    String value();
}
```

定义一个类Requst封装两个成员变量：请求方法requestMethod和请求路径requestPath。定义一个类handler封装两个成员变量，分别是controller类和类里面的某方法。

```java
public class Request {

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 请求路径
     */
    private String requestPath;
	//省略构造方法，get，hashcode和equals方法，下同
}

public class Handler {
    /**
     * Controller类
     */
    private Class<?> controllerClass;

    /**
     * Action方法
     */
    private Method actionMethod;
}
```

那么Requst和handler是一一对应的关系，用一个Map，ACTION_MAP存储这种映射关系。提供一个ControllerHelper类，从刚才提到的Set集合中通过isAnnotationPresent方法过滤出所有带Controller注解的class对象，也就是获取整个web应用的controller类对象，然后存入一个集合，遍历这个集合，对每一个controller class，利用反射获取类中的所有方法，遍历这些方法，判断方法是否带Action注解，如果带的话，直接从注解的参数中分离出请求方法和请求路径封装成为requst，再将这个controller类和该方法封装成为hanlder，将这个requst和hanlder的映射关系存到ACTION_MAP

```java
public class ControllerHelper {

    /**
     * 存放Request与Handler之间的映射关系
     */
    private static final Map<Request, Handler> ACTION_MAP = new HashMap<Request, Handler>();

    static {
        //获取所有Controller类（通过ClassHelper)
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
        if (CollectionUtil.isNotEmpty(controllerClassSet)) {
            // 遍历这些Controller类
            for (Class<?> controllerClass : controllerClassSet) {
                //获取Controller中定义的方法
                Method[] methods = controllerClass.getDeclaredMethods();
                if (ArrayUtil.isNotEmpty(methods)) {
                    // 遍历Controller类中的所有方法
                    for (Method method : methods) {
                        // 判断当前方法是否带Action注解
                        if (method.isAnnotationPresent(Action.class)) {
                            //从Action注解中获取url映射规则
                            Action action = method.getAnnotation(Action.class);
                            String mapping = action.value();
                            // 验证URL规则
                            if (mapping.matches("\\w+:/\\w*")) {
                                String[] array = mapping.split(":");
                                if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
                                    //获取请求方法与请求路径
                                    String requestMethod = array[0];
                                    String requestPath = array[1];
                                    Request request = new Request(requestMethod, requestPath);
                                    Handler handler = new Handler(controllerClass, method);
                                    // 初始化Action Map
                                    ACTION_MAP.put(request, handler);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
```

框架初始化时，将用于完成上述初始化功能的工具类在HelperLoader中加载：

```java
public class HelperLoader {

    public static void init(){
        Class<?>[] classList = {
                ClassHelper.class,
                BeanHelper.class,
                // aop要在IOChelper之前加载因为要通过AOP获得代理对象才能通过IOChelper进行依赖注入
                AopHelper.class,
                IocHelper.class,
                ControllerHelper.class
        };
        for (Class<?> cls : classList){
            ClassUtil.loadClass(cls.getName());
        }
    }
}
```

### 自定义请求转发器（DispatcherServlet）

以上部分都是在为这一步做准备，现在我们需要编写一个Servlet来处理所有的请求，DispatcherServlet继承HttpServlet，在重写的Service方法中，从HttpServletRequest中获取请求方法和路径，得到request，从上述存储requst和hanlder映射关系的map中拿到对应的handler，从而便拿到了处理这一个requst的controller对象和对应的Action方法

之后，需要对HttpServletRequest传入的参数进行封装，主要是调用API从HttpServletRequest获取实际传入的参数然后封装成Param对象（过程详情见DispatcherServlet代码及注解)，里面主要是一个Map。然后Param参数对拿到的Action方法进行反射调用，针对不同的返回类型View和Data还有对应的处理方法：如果是View的话跳转到JSP页面，如果是Data的话返回JSON。

```java
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherServlet.class);

    /**
     * init方法在构造器调用之后马上被调用，用来初始化Servlet，init方法在容器装入Servlet 时执行
     * Servlet容器在实例化后只调用一次init方法， init方法必须在servlet接收到任何请求之前完成
     * 只有servlet成功被init()方法初始化后，Service方法才会被调用
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        long startTime = System.currentTimeMillis();
        // 初始化Helper
        HelperLoader.init();
        // 获取ServletContext对象（用于注册Servlet),该对象全局唯一，而且工程内部的所有servlet都共享这个对象
        ServletContext servletContext = config.getServletContext();
        //注册处理JSP的Servlet，效果等同于web.xml下配置servlet,只不过此处采用编码进行配置
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(new String[]{"/index.jsp"});
        jspServlet.addMapping(new String[]{PropertiesConfigHelper.getAppJspPath() + "*"});
        //注册处理静态资源的默认Servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(new String[]{"/favicon.ico"});
        defaultServlet.addMapping(new String[]{PropertiesConfigHelper.getAppAssetPath() + "*"});

        long duration = System.currentTimeMillis()-startTime;
        LOGGER.info("初始化DispatcherServlet耗时{}ms", duration);
    }

    /**
     * service方法是接口中的方法，servlet容器把所有请求发送到该方法，该方法默认行为是转发http请求到doXXX方法中，
     * 如果重载了该方法，默认操作被覆盖，不再进行转发操作
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        ServletHelper.init(req, resp);
        try {
            //获取请求方法与路径并进行封装
            String requestMethod = req.getMethod().toLowerCase();
            String requestPath = req.getPathInfo();
            // 获取Action处理器
            Handler handler = ControllerHelper.getHandler(requestMethod, requestPath);
            Map<Class<?>,Object> beanMap = BeanHelper.getBeanMap();
            if (handler != null) {
                // 通过hanlder获取controller类及实例
                Class<?> controllerClass = handler.getControllerClass();
                Object controllerBean = BeanHelper.getBean(controllerClass);
                // 创建请求参数对象
                Map<String, Object> paramMap = new HashMap<String, Object>();
                // 请求体类型是application/x- www-form-urlencoded时，对该类型的请求内容提供了request.getParameter()方法来获取请求参数值
                Enumeration<String> paramNames = req.getParameterNames();
                while (paramNames.hasMoreElements()) {
                    String paramName = paramNames.nextElement();
                    String paramValue = req.getParameter(paramName);
                    paramMap.put(paramName, paramValue);
                }
                // 请求体内容是其它类型时，比如 multipart/form-data或application/json时，
                // 无法通过request.getParameter()获取到请求内容，此时只能通过request.getInputStream()，先将URL解码再从输入流中获取字符串进行解析
                // request.getInputStream()返回请求内容字节流，多用于文件上传
                String body = CodecUtil.decodeURL(StreamUtil.getString(req.getInputStream()));
                if (StringUtil.isNotEmpty(body)) {
                    String[] params = StringUtil.splitString(body, "&");
                    if (ArrayUtil.isNotEmpty(params)) {
                        for (String param : params) {
                            String[] array = StringUtil.splitString(param, "=");
                            if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
                                String paramName = array[0];
                                String paramValue = array[1];
                                paramMap.put(paramName, paramValue);
                            }
                        }
                    }
                }
                Param param = new Param(paramMap);
                Object res;
                // 通过反射调用Action方法
                Method actionMethod = handler.getActionMethod();
                if (!param.isEmpty()) {
                    res = ReflectionUtil.invokeMethod(controllerBean, actionMethod, param);
                } else {
                    res = ReflectionUtil.invokeMethod(controllerBean, actionMethod);
                }
                // 处理Action方法返回值
                if (res instanceof View) {
                    //返回JSP页面
                    View view = (View) res;
                    String path = view.getPath();
                    if (StringUtil.isNotEmpty(path)) {
                        // response.sendRedirect(String location)方法中的参数location，如果不以“/”开头，
                        // 表示相对于当前源组件的路径；如果以“/”开头，表示相对于当前服务器根路径的URL
                        if (path.startsWith("/")) {
                            resp.sendRedirect(req.getContextPath() + path);
                        } else {
                            Map<String, Object> model = view.getModel();
                            for (Map.Entry<String, Object> entry : model.entrySet()) {
                                req.setAttribute(entry.getKey(), entry.getValue());
                            }
                            req.getRequestDispatcher(PropertiesConfigHelper.getAppJspPath() + path).forward(req, resp);
                        }
                    }
                } else if (res instanceof Data) {
                    //若为Data类型，则返回JSON数据
                    Data data = (Data) res;
                    Object model = data.getModel();
                    if (model != null) {
                        resp.setContentType("application/json");
                        resp.setCharacterEncoding("UTF-8");
                        // 这个out对象的作用是可以通过当前HttpServletResponse以流的方式响应数据到请求html或者jsp页面，可以在客户端输出
                        PrintWriter writer = resp.getWriter();
                        String json = JsonUtil.toJson(model);
                        writer.write(json);
                        writer.flush();
                        writer.close();

                    }
                }
            }
        }catch (Exception e){
            LOGGER.error("DispatcherServlet错误",e);
            throw new RuntimeException(e);
        }finally {
            ServletHelper.destory();
        }
        long duration = System.currentTimeMillis()-startTime;
        LOGGER.info("DispatcherServlet service执行耗时{}ms",duration );
    }
```

### 为框架添加AOP功能

> AOP，即面向切面编程。切面是AOP里面的一个重要术语，他表示从业务逻辑里面分离出来的横切逻辑，比如性能监控，日志记录，权限控制,异常处理等，这些业务都是横向分散在所有对象层次中的，与对象的核心功能毫无关系，可以从核心业务逻辑里面抽离出去。通过AOP技术，可以将横切逻辑封装成为可重用的模块独立进行维护，减少系统的重复代码，解决代码耦合问题，让职责更加单一，开发人员更加关注核心代码的编写和维护。
>
> https://blog.csdn.net/u011402896/article/details/80369220

其他术语：

- 切点：我们需要通过某些条件去匹配所需要拦截的类，这个条件在AOP中称为切点
- 连接点：被拦截到的方法  
- 通知(Advice):在特定的连接点，AOP框架执行的动作.前置/后置/例外/最终/环绕通知
- 织入：对方法的增强叫做织入

本框架基于自定义注解和CGLIB动态代理实现AOP功能：

定义切面类注解@Aspect，他是类级别的注解，用于表明一个类属于切面类，内部有一个注解类型的成员value，该成员用于指定被拦截的类。如果我在某个切面类上定义@Aspect(Controller.class)，表示该切面只会拦截所有被@Controller注解的类

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {

    /**
     * 注解
     */
    Class<? extends Annotation> value();
}
```

为了实现切面类，我们定义一个抽象切面类abstractProxy，作为模板提供一些钩子方法如begin、end、after、before等方法或者说是切面逻辑供切面类进行扩展，还提供了一个比较关键的方法doProxy，在doProxy方法中对这些钩子方法的执行顺序进行一个整合，中间还会继续调用下一个切面类的doProxy方法，这其实就是所谓的责任链模式，因为我们知道一个连接点可能会被多个切面进行代理，要按切面的添加顺序依次执行切面的代理方法，就要用到**责任链模式**。

```java
public abstract class AspectProxy implements Proxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(AspectProxy.class);

    @Override
    public final Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object result = null;

        Class<?> cls = proxyChain.getTargetClass();
        Method method = proxyChain.getTargetMethod();
        Object[] params = proxyChain.getMethodParams();

        begin();
        try {
            if (intercept(cls, method, params)) {
                before(cls, method, params);
                result = proxyChain.doProxyChain();
                after(cls, method, params, result);
            } else {
                result = proxyChain.doProxyChain();
            }
        } catch (Exception e) {
            LOGGER.error("proxy failure", e);
            error(cls, method, params, e);
            throw e;
        } finally {
            end();
        }
        return result;
    }

    public void end() {
    }

    public void error(Class<?> cls, Method method, Object[] params, Throwable e) {
    }

    public void after(Class<?> cls, Method method, Object[] params, Object result) throws Throwable {
    }

    public void before(Class<?> cls, Method method, Object[] params) throws Throwable {
    }

    public boolean intercept(Class<?> cls, Method method, Object[] params) throws Throwable {
        return true;
    }

    public void begin() {
    }
}
```

然后我们利用CGLIB动态代理，在重写的intercept方法里面启动这条责任链为被代理类执行代理，然后通过Enhancer类的静态方法create创建这么一个代理对象,然后将这个代理对象和对应的class对象以key value的形式存入bean容器，那么在调用getBean方法从bean容器中取出某个class对象实例的时候实际上取到的是被AOP织入增强后的代理类对象。

```java
public class ProxyManager {

    /**
     * Cglib动态代理
     *
     * @param targetClass 被代理类
     * @param proxyList 代理对象列表
     * @param <T> 被代理类实例
     * @return 被代理类实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(final Class<?> targetClass, final List<Proxy> proxyList){
        return (T) Enhancer.create(targetClass, new MethodInterceptor() {
            @Override
            public Object intercept(Object targetObject, Method targetMethod, Object[] methodParams, MethodProxy methodProxy) throws Throwable {
                return new ProxyChain(targetClass,targetObject,targetMethod,methodProxy,methodParams,proxyList).doProxyChain();
            }
        });
    }
}
```

所以在框架初始化的时候，我们要去获取一个Map,他维护了代理类和被代理类集合的映射关系（因为一个代理类可能对多个目标类进行代理），所以这个Map的类型是这样的：Map<代理对象，Set<被代理类>),然后再遍历这个Map，对每一个Entry而言再去遍历value，也就是Set<被代理类>，最终会获取另一个Map<被代理类，List<代理类>)，也就是被代理类与代理对象列表之间的映射关系（见AopHelper.class)，然后在CGLIB动态代理中传入这个List，触发责任链完成最终的代理然后存入bean容器

### **实现事务控制特性**

创建一个自定义注解类@Transaction,他是方法级别的注解，表明某个方法存在事务，那么其实一般认为凡是对数据库一变更的方法都应该带上事务注解，这样的话方便更新操作失败后进行回滚。

创建一个自定义注解@Service,他是类级别的注解，表明是一个服务类，里面包含的方法可能带有Transaction注解

然后提供一个事务操作工具类，里面封装的是JDBC里关于事务的一些常用操作，比如开启事务，提交事务，回滚事务。

利用自身实现的AOP框架编写事务代理切面类，在doProxy方法里面整合开启事务，提交事务等方法的执行顺序，在catch语句中执行回滚事务操作。那么最终生成的代理对象对应的方法就是带有了事务特性的。

事务控制代理其实本质也是一个普通的AOP切面类，只不过他的横切逻辑，或者说钩子方法是关于事务的一系列操作，所以他的执行流程跟一般的AOP没有太大区别，在AOP中，我们把带@Service的类也作为切面类放入上述Map

为保证同一个线程中事务控制相关的逻辑只会执行一次，可以使用ThreadLocal封装一个boolean型的flag变量，初始化为false，在执行事务操作的时候会进行一次判断，不为true的时候才执行。

```java
/**
 * 事务代理
 *
 * @author xctian
 * @date 2020/1/28
 */
public class TransactionProxy implements Proxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionProxy.class);

    private static final ThreadLocal<Boolean> FLAG_HOLDER = new ThreadLocal<Boolean>(){
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    @Override
    public Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object res;
        boolean flag = FLAG_HOLDER.get();
        Method method = proxyChain.getTargetMethod();
        // 被Transaction注解的方法才被拦截
        if(!flag && method.isAnnotationPresent(Transaction.class)){
            FLAG_HOLDER.set(true);
            try {
                DatabaseHelper.beginTransaction();
                LOGGER.debug("begin transaction");
                res = proxyChain.doProxyChain();
                DatabaseHelper.commitTransaction();
                LOGGER.debug("commit transaction");
            }catch (Exception e){
                DatabaseHelper.rollbackTransaction();
                LOGGER.debug("rollback transaction");
                throw e;
            }finally {
                FLAG_HOLDER.remove();
            }
        }else {
            res = proxyChain.doProxyChain();
        }
        return res;
    }
}
```

