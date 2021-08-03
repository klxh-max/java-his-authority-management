package controller;

import domain.PageInfo;
import service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/findUser")
public class FindUserController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String page=req.getParameter("page");
        String rows=req.getParameter("rows");
        UserService userService=new UserService();
        PageInfo pageInfo=userService.findUserByPage(Integer.parseInt(page),Integer.parseInt(rows));
        req.setAttribute("pageInfo",pageInfo);
        req.getRequestDispatcher("users.jsp").forward(req,resp);
    }
}
