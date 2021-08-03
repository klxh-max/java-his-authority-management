package util;


import domain.Token;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//自动登录过滤器
public class AutoLoginFilter extends HttpFilter implements Filter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String path = request.getServletPath();
        if ((path.endsWith(".js")) || path.contains("logout") || path.endsWith("checkcode") || path.contains(".css") || path.contains(".jpg")) {
            //不需要认证，放过此次请求，继续请求目标
            chain.doFilter(request, response);//放行
            return;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {//有cookie
            for (Cookie c : cookies) {
                String name = c.getName();
                String value = c.getValue();//tokenId值
                if ("tokenId".equals(name)) {
                    //value==uuid，有自动登录处理
                    Token token = (Token) request.getServletContext().getAttribute(value);
                    if (token == null) {
                        //伪造的tokenId，不能自动登录
                        chain.doFilter(request, response);
                        return;
                    } else if (!token.getIp().equals(request.getRemoteAddr())) {//ip地址不一样
                        chain.doFilter(request, response);
                        return;
                    } else if (token.getEnd() < System.currentTimeMillis()) {//已经超过自动登录时效
                        chain.doFilter(request, response);
                        return;
                    } else {//符合自动登录条件
                        request.getSession().setAttribute("loginUser", token.getUser());
                        if (path.contains("login.jsp")) {//此次请求正要访问登录页，但已经实现自动登录，所以直接跳转到主界面
                            response.sendRedirect("http://localhost:8080/qxgl07/main.jsp");
                            return;
                        }
                        chain.doFilter(request, response);
                        return;
                    }
                }

            }
        }
        chain.doFilter(request, response);
    }
}
