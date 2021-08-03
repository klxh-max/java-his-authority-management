package util;

import myweb.MvcInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class TestInterceptor implements MvcInterceptor {
    @Override
    public boolean prevHandle(HttpServletRequest request, HttpServletResponse response, Method method) {
        System.out.println("测试拦截器 前");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("测试拦截器 后");
    }
}
