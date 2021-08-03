package myweb;

import myweb.annotation.RequestParam;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * 链对象，负责管理和调用 切面对象+目标controller对象
 */
public class Chain {
    private List<InterceptorInfo> aspects;//链要执行的切面
    private MappingInfo info;//装载请求对应的Controller ， Method

    private HttpServletRequest request ;
    private HttpServletResponse response ;

    private String multipartEncoding ;

    public Chain(List<InterceptorInfo> aspects, MappingInfo info, HttpServletRequest req, HttpServletResponse resp, String multipartEncoding) {
        this.aspects = aspects;
        this.info = info;
        this.request = req;
        this.response = resp;
        this.multipartEncoding = multipartEncoding;
    }

    public Object execute() throws InvocationTargetException, IllegalAccessException, InstantiationException {
        //先执行切面
        if(aspects.size() > 0){
            InterceptorInfo interceptorInfo= aspects.remove(0);
            //判断此次请求是否需要执行当前的拦截器
            String path= info.getPath();//此次请求
            Set<String> includeUrl=interceptorInfo.getIncludeUrl();
            Set<String> excludeUrl=interceptorInfo.getExcludeUrl();
            int flag = -1 ;//表示执行与否的状态 -1不执行，1执行 ， 0 抛异常

            if(includeUrl.size() == 0 && excludeUrl.size() ==0){
                flag = 1 ;
            }

            if(includeUrl.contains(path)){
                //需要执行
                flag = 1 ;
            }
            if(excludeUrl.contains(path)){
                //是一个排除在外的请求，不执行拦截器
                if(flag == 1){
                    //之前include中已经包含这个请求，结果exclude还包含
                    flag = 0 ;
                    throw new RuntimeException("混乱的拦截器配置,你是不是搞事情") ;
                }
                flag = -1 ;
            }else{
                //不需要排除在外，要执行拦截器
                if(excludeUrl.size() > 0) {
                    flag = 1;
                }
            }

            if (flag==-1){
                //当前拦截器不执行，继续看看下一个拦截器是否执行
                return this.execute();
            }else {
                //当前拦截器需要执行
                MvcInterceptor interceptor = (MvcInterceptor) interceptorInfo.getInterceptor();
                boolean f= interceptor.prevHandle(request,response, info.getMethod());
                if(f==false){
                    //终止调用
                    return null;
                }else {
                    //继续调用
                    Object result= this.execute();//调用下一个拦截器/切面 等价于 chain.doFilter()
                    interceptor.postHandle(request,response);
                    return result;
                }
            }
        }else {
            //证明已经没有拦截器需要执行，那就执行目标

            //获得参数
            Map<String, Object> paramMap = receiveParams(request);
            //处理参数
            Object[] paramValues = handleParam(paramMap, info, request, response);
            //调用mappinginfo中的对象的方法，传递参数
            Object result = info.getMethod().invoke(info.getController(), paramValues);
            return result;
        }

    }

    /*
    接受请求传递的参数，将参数处理后装入map
    map.key就是请求传递参数key
    map.value可能是一个String[] 也可以是一个文件[]--Object表示
     */
    private Map<String, Object> receiveParams(HttpServletRequest request) {
        Map<String, Object> paramMap = new HashMap<>();
        //参数有两种可能（普通请求、文件上传请求）
        //普通请求按照文件上传方式处理，会抛出异常（选择这种为突破口）
        //文件上传请求按照普通请求处理会得不到参数
        try {
            //假设是文件上传方式传递参数
            DiskFileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List<FileItem> fis = upload.parseRequest(request);//如果不是文件上传请求会抛出异常
            for (FileItem fileItem : fis) {
                //拿到了一个参数
                if (fileItem.isFormField()) {
                    //是一个普通参数
                    String key = fileItem.getFieldName();
                    String value = fileItem.getString(multipartEncoding);

                    String[] values = (String[]) paramMap.get(key);
                    if (values == null) {
                        values = new String[]{value};
                    } else {
                        values = Arrays.copyOf(values, values.length + 1);
                        values[values.length - 1] = value;
                    }
                    paramMap.put(key, values);
                } else {
                    //是一个文件参数
                    String key = fileItem.getFieldName();
                    String fileName = fileItem.getName();//文件名
                    long size = fileItem.getSize();//文件大小
                    String contentType = fileItem.getContentType();//文件类型
                    InputStream is = fileItem.getInputStream();//文件内容
                    MultipartFile multipartFile = new MultipartFile(fileName, size, contentType, is);
                    MultipartFile[] mf = (MultipartFile[]) paramMap.get(key);
                    if (mf == null) {
                        mf = new MultipartFile[]{multipartFile};
                    } else {
                        mf = Arrays.copyOf(mf, mf.length + 1);
                        mf[mf.length - 1] = multipartFile;
                    }
                    paramMap.put(key, mf);
                }
            }
        } catch (FileUploadException | UnsupportedEncodingException e) {
            //出现异常表示不是文件上传请求方式，按照普通请求方式处理
            Enumeration<String> names = request.getParameterNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                String[] values = request.getParameterValues(name);
                paramMap.put(name, values);
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return paramMap;
    }

    /*
    按照请求映射关系要调用的那个目标方法，获得目标方法的参数列表
    根据参数列表获得所需要的参数，并将参数组装到Object[]中
     */
    private Object[] handleParam(Map<String, Object> paramMap, MappingInfo info, HttpServletRequest req, HttpServletResponse resp) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Method method = info.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] paramValues = new Object[parameters.length];
        int i = 0;
        for (Parameter parameter : parameters) {
            RequestParam rp = parameter.getAnnotation(RequestParam.class);
            Class paramType = parameter.getType();
            if (rp != null) {
                //注解的value就是key，可以用来获取实参
                String key = rp.value();
                Object value = paramMap.get(key);
                if (value == null) {
                    //当前所需参数为空
                    i++;
                    continue;
                }
                //如果参数值存在，起初只有2种表现形成 String[] , MulitpartFile[]
                //将此次需要的参数value，转换成方法中定义的需要参数类型
                paramValues[i++] = castType(value, paramType);
            } else {
                //没有@RequestParam注解，可能是request、response、session、domain（组装参数）
                if (paramType == HttpServletRequest.class) {
                    paramValues[i++] = req;
                } else if (paramType == HttpServletResponse.class) {
                    paramValues[i++] = resp;
                } else if (paramType == HttpSession.class) {
                    paramValues[i++] = req.getSession();
                } else {
                    Object paramObject = paramType.newInstance();
                    Method[] methods = paramType.getMethods();
                    //通过反射获得对象中的属性名，根据属性名找到与之同名的参数，为属性赋值
                    //为了更好的符合Java封装的特性，不建议通过反射找属性，建议找set方法
                    for (Method m : methods) {
                        String mname = m.getName();
                        if (mname.startsWith("set")) {
                            String key = mname.substring(3);
                            key = key.substring(0, 1).toLowerCase() + key.substring(1);
                            Object value = paramMap.get(key);
                            if (value == null) {
                                continue;
                            }
                            Class domainParamType = m.getParameterTypes()[0];
                            Object obj = castType(value, domainParamType);
                            m.invoke(paramObject, obj);
                        }
                    }
                    //循环结束找到了所有set方法，赋值结束，将对象保存
                    paramValues[i++] = paramObject;
                }
            }
        }
        return paramValues;
    }

    /*
    将原始类型的参数数据，转换成目标方法所需要的类型
    原始类型：String[],MultipartFile[]
    目标类型：int、long、double、String、Integer、MultipartFile以及这些类型的数组形式
     */
    private Object castType(Object value, Class paramType) {
        if (paramType == String.class) {
            String str = ((String[]) value)[0];
            return str;
        }
        if (paramType == int.class || paramType == Integer.class) {
            String str = ((String[]) value)[0];
            int num = Integer.parseInt(str);
            return num;
        }
        if (paramType == long.class || paramType == Long.class) {
            String str = ((String[]) value)[0];
            long num = Long.parseLong(str);
            return num;
        }
        if (paramType == double.class || paramType == Double.class) {
            String str = ((String[]) value)[0];
            double num = Double.parseDouble(str);
            return num;
        }
        if (paramType == String[].class) {
            return value;
        }
        if (paramType == int[].class) {
            String[] str = (String[]) value;
            int[] n = new int[str.length];
            for (int j = 0; j < str.length; j++) {
                n[j] = Integer.parseInt(str[j]);
            }
            return n;
        }
        if (paramType == Integer[].class) {
            String[] str = (String[]) value;
            Integer[] nums = new Integer[str.length];
            for (int j = 0; j < str.length; j++) {
                nums[j] = Integer.valueOf(str[j]);
            }
            return nums;
        }
        if (paramType == MultipartFile.class) {
            return ((MultipartFile[]) value)[0];
        }
        if (paramType == MultipartFile[].class) {
            return value;
        }
        return null;
    }

}
