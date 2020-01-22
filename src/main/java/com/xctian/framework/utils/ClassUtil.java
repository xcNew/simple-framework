package com.xctian.framework.utils;

import com.xctian.framework.helper.PropertiesConfigHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类操作工具类
 *
 * @author xctian
 * @date 2020/1/18
 */
public class ClassUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CastUtil.class);

    /**
     * 获取类加载器
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 加载类
     *
     * @param className 待加载的类的全限定名
     * @return 加载后的类
     */
    public static Class<?> loadClass(String className, boolean isInitialized) {
        Class<?> cls;
        try {
            cls = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.error("类加载失败", e);
            throw new RuntimeException(e);
        }
        return cls;
    }

    /**
     * 加载类,默认初始化类
     */
    public static Class<?> loadClass(String className) {
        return loadClass(className, true);
    }

    /**
     * 获取指定包下的所有类
     *
     * @param packageName 指定包的全限定名
     * @return 该包下的所有类组成的集合
     */
    public static Set<Class<?>> getClassSet(String packageName) {
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        try {
            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol();
                    // 如果url是file类型
                    if (protocol.equals("file")) {
                        //每个url path对应的限定名
                        String packagePath = url.getPath().replaceAll("%20", " ");
                        addClass(classSet, packagePath, packageName);
                        // 如果url是jar类型
                    } else if (protocol.equals("jar")) {
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        if (jarURLConnection != null) {
                            JarFile jarFile = jarURLConnection.getJarFile();
                            if (jarFile != null) {
                                Enumeration<JarEntry> jarEntries = jarFile.entries();
                                while (jarEntries.hasMoreElements()) {
                                    JarEntry jarEntry = jarEntries.nextElement();
                                    String jarEntryName = jarEntry.getName();
                                    if (jarEntryName.endsWith(".class")) {
                                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                        doAddClass(classSet, className);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取指定包下的类集合失败", e);
            throw new RuntimeException(e);
        }
        return classSet;
    }

    /**
     * 加载url path对应的所有类并存入集合，如果url path对应文件夹则进行递归
     *
     * @param classSet    包名下的类集合
     * @param packagePath 每个url path对应的全限定名
     * @param packageName 包名
     */
    private static void addClass(Set<Class<?>> classSet, String packagePath, String packageName) {
        File[] files = new File(packagePath).listFiles(new FileFilter() {
            // 过滤出文件夹方便后序递归调用，或者直接过滤出.class结尾的文件
            @Override
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class") || file.isDirectory());
            }
        });
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) {
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                if (StringUtil.isNotEmpty(packageName)) {
                    className = packageName + "." + className;
                }
                doAddClass(classSet, className);
            } else {
                // 递归对文件夹进行操作
                String subPackagePath = fileName;
                if (StringUtil.isNotEmpty(packagePath)) {
                    subPackagePath = packagePath + "/" + subPackagePath;
                }
                String subPackageName = fileName;
                if (StringUtil.isNotEmpty(packageName)) {
                    subPackageName = packageName + "/" + subPackageName;
                }
                addClass(classSet, subPackagePath, subPackageName);
            }

        }
    }

    private static void doAddClass(Set<Class<?>> classSet, String className) {
        Class<?> clz = loadClass(className, false);
        classSet.add(clz);
    }

    // test case
//    public static void main(String[] args) {
//        String packageName = "com.xctian.framework";
//        try {
//            URL url1 = PropertiesConfigHelper.class.getResource("/new2.class");
//            System.out.println("url1:"+url1);
//            URL url2 = ClassUtil.class.getResource("new2.class");
//            System.out.println("url2:"+url2);
//            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".","/"));
//            while(urls.hasMoreElements()){
//                URL url = urls.nextElement();
//                // getPath()去掉了protoco
//                String path = url.getPath().replaceAll("%20"," ");
//                File[] files = new File(path).listFiles();
//                System.out.println("files大小："+ files.length);
//                try {
//                    for(File each :files){
//                        if(each.isDirectory()){
//                            System.out.println(each.getName()+"是文件夹");
//                        }else{
//                            System.out.println(each.getName()+"不是文件夹");
//                        }
//                        System.out.println(each.getName());
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//                System.out.println(url.getPath().toString());
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
}
