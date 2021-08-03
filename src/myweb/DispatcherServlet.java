package myweb;

import com.alibaba.fastjson.JSON;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import myweb.annotation.Controller;
import myweb.annotation.RequestMapping;
import myweb.annotation.RequestParam;
import myweb.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class DispatcherServlet extends HttpServlet {
    /*
    缓存请求映射信息
    key="/test1"
     */
    Map<String, MappingInfo> infoMap = new HashMap<>();
    /*
    单实例存储controller对象
    key="com.controller.TestController"
    value=new Object
     */
    Map<String, Object> controllerMap = new HashMap<>();

    /**
     * 读取各种配置信息
     * （目前只有请求映射信息）
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        //目前配置信息来自于两个部分，而且两个部分都可能存在

        String xmlPath = super.getInitParameter("classpath");
        if (xmlPath != null && !"".equals(xmlPath)) {
            //指定了配置文件，需要读取配置文件
            readXml(xmlPath);
        }

        String packagePath = super.getInitParameter("controller-scan");
        if (packagePath != null && !"".equals(packagePath)) {
            //指定了包路径，需要读取包下类中的注解信息
            try {
                readAnnotation(packagePath);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    //读取xml配置文件
    public void readXml(String path) {
        //我们要的完整路径
        String realPath = Thread.currentThread().getContextClassLoader().getResource(path).getPath();
        try {
            InputStream is = new FileInputStream(realPath);
            //使用dom,sax读取xml内容
            SAXReader reader = new SAXReader();
            Document document = reader.read(is);
            //解析mapping标签
            parseMappingElement(document);

            //解析multipart-encoding标签
            parseMultipartEncodingElement(document);

            //解析mvc-interceptor标签
            parseMvcInterceptorElement(document);

            //解析其他标签
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //读取xml中<mapping>请求映射信息
    private void parseMappingElement(Document document) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        //按照标签的嵌套规则，找到需要的所有mapping标记
        List<Element> mappingElements = document.selectNodes("mvc/mapping");
        for (Element element : mappingElements) {
            /**
             * 获取标签指定的属性值,标签中记载着请求与响应对应关系
             * 此时（初始化）获得这个对应关系是为了后面请求的时候用
             * 所以需要存储起来
             */
            String path = element.attributeValue("path");
            String className = element.attributeValue("class");
            String methodName = element.attributeValue("method");

            //反射创建目标对象
            Class clazz = Class.forName(className);
            Object obj = getSingleController(className);

            //通过反射，根据方法名获取方法对象,方法可能存在重载,参数在<type></type>标签里面
            List<Element> typeElements = element.selectNodes("type");
            Class[] types = new Class[typeElements.size()];
            int i = 0;
            for (Element typeEle : typeElements) {
                String typeStr = typeEle.getText();
                types[i++] = castStringToClass(typeStr);
            }
            Method method = clazz.getMethod(methodName, types);

            MappingInfo info = new MappingInfo(path, obj, method);
            System.out.println(method);
            infoMap.put(path, info);
        }

    }

    private String multipartEncoding = "";

    //读取xml中<multipart-encoding>信息
    private void parseMultipartEncodingElement(Document document) {
        //按照标签的嵌套规则，找到需要的所有mapping标记
        Element multipartEncodingElement = (Element) document.selectSingleNode("mvc/multipart-encoding");
        if (multipartEncodingElement == null) {
            return;
        }
        multipartEncoding = multipartEncodingElement.getText();
    }

    private List<InterceptorInfo> interceptorInfoList=new ArrayList<>();

    //读取xml中<mvc-interceptor>信息
    private void parseMvcInterceptorElement(Document document) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        //按照标签的嵌套规则，找到需要的所有mapping标记
        List<Element> mvcInterceptorElement = (List<Element>) document.selectNodes("mvc/mvc-interceptor");
        for (Element element : mvcInterceptorElement) {
            /**
             * 获取标签指定的属性值,标签中记载着请求与响应对应关系
             * 此时（初始化）获得这个对应关系是为了后面请求的时候用
             * 所以需要存储起来
             */
            String className = element.attributeValue("class");

            //反射创建目标对象
            Object interceptor= Class.forName(className).newInstance();
            InterceptorInfo interceptorInfo=new InterceptorInfo();
            interceptorInfo.setInterceptor(interceptor);

            Element includeEle=(Element) element.selectSingleNode("include");
            Element excludeEle=(Element) element.selectSingleNode("exclude");
            if(includeEle!=null){
                String includeStr=includeEle.getText();
                interceptorInfo.setIncludeUrl(includeStr);
            }
            if(excludeEle!=null){
                String excludeStr=excludeEle.getText();
                interceptorInfo.setExcludeUrl(excludeStr);
            }
            interceptorInfoList.add(interceptorInfo);

        }
    }

    //将字符串表示的类型转换成对应的Class
    private Class castStringToClass(String typeStr) throws ClassNotFoundException {
        if ("int".equals(typeStr)) {
            return int.class;
        }
        if ("long".equals(typeStr)) {
            return long.class;
        }
        if ("double".equals(typeStr)) {
            return double.class;
        }
        //除了8种基本类型（这里只列举了三种），其余引用类型写的都是全名，可以反射生成Class
        return Class.forName(typeStr);
    }

    public Object getSingleController(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Object obj = controllerMap.get(getClass());
        if (obj == null) {
            obj = Class.forName(className).newInstance();
            controllerMap.put(className, obj);
        }
        return obj;
    }

    //读取注解
    public void readAnnotation(String packagePath) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        //需要框架从指定的包（可能有多个）中找到请求映射的直接
        String[] packagePathArray = packagePath.split(",");
        for (String p : packagePathArray) {
            /*
             * 如果想要用过反射获得类，光有包路径还不够，还需要类名com.controller+TestController
             * 想要获得类名，反射行不通，可以通过File操作获得文件夹中的文件名（去掉后缀就是类名）
             */
            String path1 = p.replace(".", "/");
            String realPath = "";
            try {
                realPath = Thread.currentThread().getContextClassLoader().getResource(path1).getPath();
            } catch (NullPointerException e) {
                //指定的包路径有错误
                System.out.println("logging : package path not found [" + realPath + "]");
                //当前包不存在就不检索了
                continue;
                //throw new FileNotFoundException(realPath);
            }
            File dir = new File(realPath);
            String className = "";
            //获得文件夹中所有子内容名字（子文件夹，子文件）
            String[] fnames = dir.list();
            for (String fname : fnames) {
                if (fname.endsWith(".class")) {
                    className = fname.replace(".class", "");
                    className = p + "." + className;
                    Class clazz = Class.forName(className);
                    Controller c = (Controller) clazz.getAnnotation(Controller.class);
                    if (c == null) {
                        //该类没找到@Controller注解，不需要扫描
                        continue;
                    }
                    //遍历类中所有方法，找到@RequestMapping
                    Method[] methods = clazz.getMethods();
                    for (Method method : methods) {
                        RequestMapping rm = method.getAnnotation(RequestMapping.class);
                        if (rm == null) {
                            continue;
                        }
                        String path = rm.value();
                        Object obj = getSingleController(className);
                        MappingInfo info = new MappingInfo(path, obj, method);
                        Object obj1 = infoMap.get(path);
                        if (obj1 != null) {
                            //当前映射关系已经存在
                            //去重 continue
                            //覆盖 map.put
                            //抛出异常 throw new Exception
                        }
                        infoMap.put(path, info);
                    }
                }
            }
        }

    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //请求映射，根据请求调用controller的具体方法

        //获得请求  /test1
        //req.getRequestURL();//http://localhost:8080/web-demo/test1
        String uri = req.getRequestURI();// /web-demo/test1
        String root = req.getContextPath();// /web-demo
        String path = uri.replace(root, "");
        //String path2=req.getServletPath();// /test1 但如果发送请求是斜杠，那么默认为index.html

        //根据请求找到对应的mappinginfo  映射信息
        MappingInfo info = infoMap.get(path);
        if (info == null) {
            /*
            没有找到与之匹配的映射请求（xml和注解）
            可能是没有需要调用的controller和方法，也可能是请求一些文件资源（.html .css .jpg .js）
            之前没有框架时，可以访问这些文件资源是因为Tomcat提供了一个处理静态资源访问的DefaultServlet
            该Servlet处理的请求模式就是 /
            现在我们的DispatcherServlet一般也建议配置成 /
            这就会覆盖Tomcat提供的，所以需要框架来处理静态资源
             */
            handleStaticResource(path, req, resp);
        } else {
            //找到映射信息了，就按照映射信息分发请求，调用对应方法
            try {
                handleDynamicResource(info, req, resp);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }

    }

    //处理动态资源请求
    private void handleDynamicResource(MappingInfo info, HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, InvocationTargetException, InstantiationException, IOException, ServletException {
        //需要带切面处理
        //使用责任链模式，实现切面与目标的调用
        List<InterceptorInfo> cloneList=new ArrayList<>();
        cloneList.addAll(interceptorInfoList);
        Chain chain=new Chain(cloneList,info,request,response,multipartEncoding);
        Object result=chain.execute();

        //根据result返回值实现响应
        handleResponse(result, info.getMethod(), request, response);
    }

    private void handleResponse(Object result, Method method, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if(result==null){
            //不需要框架响应
            return;
        }
        ResponseBody rb=method.getAnnotation(ResponseBody.class);
        if(rb!=null){
            //直接响应
            if(result instanceof String||result instanceof Integer||result instanceof Long||result instanceof Boolean){
                //简单类型，直接以字符串形式响应
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(result.toString());
            }else{
                //不是简单类型，集合对象需要转换成json再响应
                String json= JSON.toJSONString(result);
                response.setContentType("text/json;charset=utf-8");
                response.getWriter().write(json);
            }
        }else{
            //间接响应
            if(result instanceof String){
                //不需要携带数据
                String _result=(String) result;
                if(_result.startsWith("redirect:")){
                    //重定向
                    _result=_result.replace("redirect:","");
                    response.sendRedirect(_result);
                }else{
                    //转发
                    request.getRequestDispatcher(_result).forward(request,response);
                }
            }else {
                //需要携带数据
                ModelAndView _result=(ModelAndView) result;
                String path=_result.getViewName();
                if(path.startsWith("redirect:")){
                    //重定向
                    path=path.replace("redirect:","");
                    path+="?";

                    Set<String>names=_result.getObjectNames();
                    for(String name:names){
                        Object value=_result.getObject(name);
                        path+=name+"="+value+"&";
                    }
                    path=path.substring(0,path.length()-1);
                    response.sendRedirect(path);
                }else {
                    //转发
                    Set<String>names=_result.getObjectNames();
                    for(String name:names){
                        Object value=_result.getObject(name);
                        request.setAttribute(name,value);
                    }
                    request.getRequestDispatcher(path).forward(request,response);
                }
            }
        }
    }



    //处理静态资源，在mapping映射信息中没有找到就表示静态资源
    private void handleStaticResource(String path, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if ("/".equals(path)) {
            //获得默认资源
            path = request.getServletPath();
        }
        //path是相对路径，通过path我们获取绝对路径
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String realPath = request.getServletContext().getRealPath(path);
        File file = new File(realPath);
        if (!file.exists()) {
            //资源不存在
            response.sendError(404, "[" + path + "]");
            return;
        }
        InputStream is = new FileInputStream(file);
        OutputStream os = response.getOutputStream();
        byte[] bs = new byte[256];
        while (true) {
            int length = is.read(bs);
            if (length == -1) {
                break;
            }
            os.write(bs, 0, length);
            os.flush();
        }
        is.close();
    }
}
