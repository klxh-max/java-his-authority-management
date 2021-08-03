package myweb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public interface MvcInterceptor {
    //编写请求处理之前需要切面执行的操作
    //根据业务处，需要继续处理请求，返回true；终止此次请求处理，返回false
    public boolean prevHandle(HttpServletRequest request, HttpServletResponse response, Method method);

    //编写请求处理之后需要切面执行的操作
    public void postHandle(HttpServletRequest request, HttpServletResponse response);
}
