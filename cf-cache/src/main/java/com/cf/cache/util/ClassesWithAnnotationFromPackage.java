package com.cf.cache.util;

import com.cf.cache.aop.EnableCFCache;
import com.cf.cache.vo.CacheTasks;
import com.google.common.collect.ArrayListMultimap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 项目启动时，加载含有EnableCFCache注解的所有方法（必须fix这个值>0，同时要求uuid唯一，这样保证mar存的key与value是实际与方法一一对应的）
 * <p>Description: </p>
 * <p>Company: yingchuang</p>
 *
 * @author lantern
 * @date 2019/4/11
 */
@Slf4j
public class ClassesWithAnnotationFromPackage {

    public static final String   SPLIT="[//||,|;|:]";

    public static  ArrayListMultimap<String,CacheTasks> getCacheTasksWithAnnotationFromPackage(String[] packageNames)
    {
       if(packageNames==null || packageNames.length<=0) return null;
        ArrayListMultimap<String,CacheTasks> arrayListMultimap=null;
        for (String packageName:packageNames) {
            ArrayListMultimap<String,CacheTasks>  temp=getCacheTasksWithAnnotationFromPackage( packageName);
            if(temp!=null && temp.size()>0) {
                if(arrayListMultimap==null) arrayListMultimap=ArrayListMultimap.create();
                arrayListMultimap.putAll(temp);
            }
        }
        return  arrayListMultimap;
    }

    public static  ArrayListMultimap<String,CacheTasks> getCacheTasksWithAnnotationFromPackage(String packageName)
    {

        Map<Method,CacheTasks> cacheTasksList =  getMethodsWithAnnotationFromPackage( packageName);
        if(cacheTasksList==null || cacheTasksList.isEmpty())
        {
            log.warn("EnableCFCache has fiex>0 and uuid is unique is empty");
            return null;
        }

        return  cacheTasksToMapList(cacheTasksList);
    }

    private static  ArrayListMultimap<String,CacheTasks>  cacheTasksToMapList( Map<Method,CacheTasks> cacheTasksList)
    {
        ArrayListMultimap<String,CacheTasks> arrayListMultimap= ArrayListMultimap.create();
        cacheTasksList.forEach((method,cacheTasks)->{
            String key =BetweenTimeKey.getByValue(cacheTasks.getEnableCFCache().fixed()).getKey();
            if("one".equalsIgnoreCase(key))
            {
                arrayListMultimap.put(key+"_"+method.toString(),cacheTasks);
            }
            else  arrayListMultimap.put(key,cacheTasks);
        });
        log.warn("EnableCFCache has fiex>0 size--->{}",arrayListMultimap.values().size());
        return arrayListMultimap;
    }


    public static  Map<Method,CacheTasks> getMethodsWithAnnotationFromPackage(String packageName) {
        Map<Method,CacheTasks> cacheTasksList = new HashMap<>();
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs ;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
        }
        catch (IOException e) {
            log.error("Failed to get resource", e);
            return null;
        }

        while (dirs!=null && dirs.hasMoreElements()) {
            URL url = dirs.nextElement();//file:/D:/E/workspaceGitub/springboot/JSONDemo/target/classes/com/yq/controller
            String protocol = url.getProtocol();//file
            //https://docs.oracle.com/javase/7/docs/api/java/net/URL.html
            //http, https, ftp, file, and jar
            //本文只需要处理file和jar
            if ("file".equals(protocol) ) {
                fileProtocol(cacheTasksList,url,packageName);
            } else if ("jar".equals(protocol)) {
                jarProtocol(cacheTasksList,url,packageName);
            }
            else {
                log.warn("can't process the protocol={}", protocol);
            }
        }
        return cacheTasksList;
    }

    private static void  jarProtocol( Map<Method,CacheTasks> cacheTasksList, URL url  ,String packageName)
    {
        JarFile jar = null;
        try {
            jar = ((JarURLConnection) url.openConnection()).getJarFile();
            //扫描jar包文件 并添加到集合中
        }
        catch (Exception e) {
            log.error("Failed to decode class jar", e);
        }
        //List<Class<?>> alClassList = new ArrayList<Class<?>>();
        if(jar!=null)
         findClassesByJar(packageName, jar, cacheTasksList);
    }

    private static void  fileProtocol( Map<Method,CacheTasks> cacheTasksList, URL url  ,String packageName)
    {
        String filePath = null;
        try {
            filePath = URLDecoder.decode(url.getFile(), "UTF-8");///D:/E/workspaceGitub/springboot/JSONDemo/target/classes/com/yq/controller
        }
        catch (UnsupportedEncodingException e) {
            log.error("Failed to decode class file", e);
        }
        if(StringUtils.isNotBlank(filePath))
        {
            filePath = filePath.substring(1);
            getMethodsWithAnnotationFromFilePath(packageName, filePath, cacheTasksList,"file");
        }
    }
    private static void findClassesByJar(String pkgName, JarFile jar, Map<Method,CacheTasks> cacheTasksList) {
        String pkgDir = pkgName.replace(".", "/");
        Enumeration<JarEntry> entry = jar.entries();

        while (entry.hasMoreElements()) {
            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文
            JarEntry jarEntry = entry.nextElement();
            String name = jarEntry.getName();
            // 如果是以/开头的
            if (name.charAt(0) == '/') {
                // 获取后面的字符串
                name = name.substring(1);
            }

            if (jarEntry.isDirectory() || !name.startsWith(pkgDir) || !name.endsWith(".class")) {
                continue;
            }
            //如果是一个.class文件 而且不是目录
            // 去掉后面的".class" 获取真正的类名
            String className = name.substring(0, name.length() - 6);
            Class<?> tempClass = loadClass(className.replace("/", "."));
            // 添加到集合中去
            if (tempClass != null) {
                getMethodsFromClassWithAnnotation(tempClass,cacheTasksList ,"jar");
            }
        }
    }

    /**
     * 加载类
     * @param fullClsName 类全名
     * @return
     */
    private static Class<?> loadClass(String fullClsName ) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(fullClsName );
        } catch (ClassNotFoundException e) {
            log.error("PkgClsPath loadClass", e);
        }
        return null;
    }

    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0;
    }
    //filePath is like this 'D:/E/workspaceGitub/springboot/JSONDemo/target/classes/com/controller'
    private static void getMethodsWithAnnotationFromFilePath(String packageName, String filePath,Map<Method,CacheTasks> cacheTasksList,String protect) {
        if(isLinux() && !filePath.startsWith("/"))
        {
            filePath="/"+filePath;
            log.info("filePath===>{}",filePath);
        }
        Path dir = Paths.get(filePath);//D:\E\workspaceGitub\springboot\JSONDemo\target\classes\com\yq\service
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for(Path path : stream) {
                String fileName = String.valueOf(path.getFileName()); // for current dir , it is 'helloworld'
                //如果path是目录的话， 此处需要递归，
                boolean isDir = Files.isDirectory(path);
                if(isDir) {
                    getMethodsWithAnnotationFromFilePath(packageName + "." + fileName , path.toString(), cacheTasksList,protect);
                }
                else  {
                    getMethodsFromClassWithAnnotation( fileName, packageName, protect,cacheTasksList);
                }
            }
        }
        catch (IOException e) {
            log.error("Failed to read class file", e);
        }
    }

    private static void  getMethodsFromClassWithAnnotation(String fileName,String packageName,String protect,Map<Method,CacheTasks> cacheTasksList)
    {
        if(!fileName.endsWith("class")) return;
        String className = fileName.substring(0, fileName.length() - 6);
        Class<?> classes = null;
        String fullClassPath = packageName + "." + className;
        try {
            if(log.isDebugEnabled())
               log.debug("fullClassPath={}", fullClassPath);
            classes = Thread.currentThread().getContextClassLoader().loadClass(fullClassPath);
        }
        catch (ClassNotFoundException e) {
            log.error("Failed to find class={}", fullClassPath, e);
        }

        if (null != classes && null != classes.getAnnotation(Service.class)) {
            getMethodsFromClassWithAnnotation(classes,cacheTasksList,protect);
            // classList.add(classes);
        }
    }

    private static  void getMethodsFromClassWithAnnotation(Class<?> classes,Map<Method,CacheTasks> list,String protect )
    {
        Method[] methods = classes.getDeclaredMethods();
        if(methods != null){
            for(Method method : methods){
                putTask( method, protect, classes, list);
            }
        }
    }
    private static void  putTask(Method method,String protect,Class<?> classes,Map<Method,CacheTasks> list)
    {
        EnableCFCache enableCFCache = method.getAnnotation(EnableCFCache.class);
        if(enableCFCache!=null &&  enableCFCache.fixed()>0)
        {
            if(list.containsKey(method))
            {
                String msg="【"+protect+"】newBen->"+classes.getName()+"."+method.getName()+" | ";
                CacheTasks cacheTasks =list.get(method);
                msg+= "【"+cacheTasks.getKey()+"】cacheBen->"+method.getDeclaringClass().getName()+"."+cacheTasks.getMethod().getName();
                log.warn(msg+" ->method 已经存在值【"+method+"】,请先检查一下，此值是不能重复的");
            }
            else
            {
                list.put(method,getCacheTasks(enableCFCache,method, classes,protect,StringUtils.isBlank(enableCFCache.fieldKey())));
            }
        }
    }


    private static CacheTasks getCacheTasks(EnableCFCache enableCFCache,Method method,Class<?>  classes,String protect,boolean noneArgs)
    {
        String signClassName=StringUtils.uncapitalize(classes.getSimpleName());
        Service service = classes.getAnnotation(Service.class);
        if(service!=null && StringUtils.isNotBlank(service.value()))
        {
            signClassName=service.value();
        }
       // String fieldKey=   enableCFCache.prefix() + parseKey(enableCFCache.fieldKey(), method, null);
        //String cacheKey =parseKey(fieldKey,  method, null);
        return  new CacheTasks(protect,method,null,enableCFCache,signClassName,noneArgs);
    }

    /**
     * 获取redis的key
     */
    public  static String parseKey(String fieldKey, Method method, Object [] args){
        if(args==null || args.length<=0 || StringUtils.isBlank(fieldKey)) return "";
        //获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer u =
                new LocalVariableTableParameterNameDiscoverer();
        String [] paraNameArr=u.getParameterNames(method);
        //使用SPEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser();
        //SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        //把方法参数放入SPEL上下文中
        for(int i=0;i<paraNameArr.length;i++){
            context.setVariable(paraNameArr[i], args[i]);
        }
        return parseKey(  context,  parser , fieldKey );
    }

    private static String parseKey( StandardEvaluationContext context, ExpressionParser parser ,String fieldKey )
    {
        StringBuilder result = new StringBuilder();
        String[] fieldKeyArr = fieldKey.split(SPLIT);
        for (String key:fieldKeyArr){
            String value = parser.parseExpression(key).getValue(context,String.class);
            if(StringUtils.isNotBlank(value))
                result.append(value).append(":");
            else
                result.append("null").append(":");
        }
        String resultStr=result.toString();
        if(resultStr.endsWith(":")) return resultStr.substring(0,resultStr.length()-1);
        return resultStr;
    }

    /**
     *
     * @param times 单位秒
     */
    public static void waiting(long times)
    {
        try {
            Thread.sleep(times*1000);
        }catch ( Exception e)
        {
            log.info("error->",e.getMessage());
            waiting( times*1000);
        }
    }
}
