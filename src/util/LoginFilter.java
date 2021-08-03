package util;

import domain.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter extends HttpFilter implements Filter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //先排除不需要认证的请求
        //自行登录操作不需要认证（需要用户手动认证）还有*.js *.jpg *.css
//        request.getRequestURL();//  http://localhost:8080/qxgl/login.view
//        request.getRequestURI();//  /qxgl/login.view
//        request.getContextPath();// /qxgl工程名
        String path = request.getServletPath();//获得此次请求 login.view
        System.out.println("path:"+path);
        if(path.contains("login")||path.contains("logout")||path.endsWith("checkcode")||(path.contains(".js")&&(!path.contains(".jsp")))||path.contains(".css")||path.contains(".jpg")){
            //不需要认证，放过此次请求，继续请求目标
            chain.doFilter(request,response);//放行
            return;
        }

        //其余请求做登录认证
        User user=(User) request.getSession().getAttribute("loginUser");
        System.out.println(user);
        if(user==null){
            //需要认证，但发现还没有登录
            response.sendRedirect("http://localhost:8080/qxgl07/login.jsp");
        }else {
            //需要认证，且认证通过，放过请求，继续访问目标
            chain.doFilter(request,response);
        }
    }
}
