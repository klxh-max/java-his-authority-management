package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getSession().removeAttribute("loginUser");//清空session
        //覆盖浏览器cookie实现清空效果
        Cookie cookie=new Cookie("tokenId","-1");
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
        resp.sendRedirect("http://localhost:8080/qxgl07/login.jsp");

    }
}
