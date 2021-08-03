package controller;

import domain.Token;
import domain.User;
import service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String uname = req.getParameter("uname");
        String upass = req.getParameter("upass");
        String autoflag = req.getParameter("autoflag");
        String code = req.getParameter("code");
        String codeCheck = "true";
        if (code == null || "".equals(code)) {
            //验证码为空
            codeCheck = "false";
        }
        String checkcode = (String) req.getSession().getAttribute("code");
        if (checkcode.equals(code) == false) {
            codeCheck = "false";
        }
        if ("false".equals(codeCheck)) {
            req.setAttribute("result", "验证码错误！");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
            return;
        }
        UserService userService = new UserService();
        User user = userService.checkLogin(uname, upass);
        resp.setCharacterEncoding("UTF-8");
        if (user == null && "true".equals(codeCheck)) {
            req.setAttribute("result", "用户名或密码错误！");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        } else {
            //自动登录实现
            if (autoflag != null && !"".equals(autoflag)) {
                String tokenId = UUID.randomUUID().toString();
                Cookie c = new Cookie("tokenId", tokenId);
                c.setMaxAge(60);//cookie在浏览器的存活时间，秒为单位，时间应与后面一致
                resp.addCookie(c);//传递令牌

                Token token = new Token(tokenId, user, req.getRemoteAddr(),
                        System.currentTimeMillis(),
                        System.currentTimeMillis() + 1000L * 20);//七天就是加上1000L*60*60*24*7
                //req.getRemoteAddr();可以获得局域网ip，广域网使用代理后还要其他方式获得

                //token在服务器中如何存储？
                //request和session都不行，数据库则有些浪费空间影响效率
                //application（服务器运行期缓存），redis缓存中
                req.getServletContext().setAttribute(tokenId, token);


            }

            HttpSession session = req.getSession();
            session.setAttribute("loginUser", user);
            //登陆成功获得登陆用户的菜单列表、按钮列表、权限范围列表
            //菜单列表最终展示在主页面左侧，用layui-tree组装（要求List集合）
            Map<String,Object> map=userService.findUserAuth(user.getUid());
            req.getSession().setAttribute("loginAuth",map);

            req.getRequestDispatcher("main.jsp").forward(req, resp);
        }
    }
}
