package controller;

import domain.User;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.ss.usermodel.*;
import service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@WebServlet("/userImport")
public class UserImportController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        DiskFileItemFactory factory=new DiskFileItemFactory();
        ServletFileUpload upload=new ServletFileUpload(factory);
        try {
            List<FileItem> itemList= upload.parseRequest(req);
            FileItem fi= itemList.get(0);
            InputStream is= fi.getInputStream();
            //Workbook相当于虚拟机版的excel，功能结构与excel一致；从book工作蒲中获取sheet工作表
            Workbook workbook= WorkbookFactory.create(is);
            Sheet sheet=workbook.getSheetAt(0);
            for(int i=1;i<sheet.getLastRowNum();i++){
                Row row= sheet.getRow(i);

                Cell c1=row.getCell(0);
                Cell c2=row.getCell(0);

                String uname=c1.toString();
                String upass=c2.toString();//读取excel是所有数字形式的数据都会变成浮点型
                upass=uname.replace(".0","");
                User user=new User(null,uname,upass,null,null);
                UserService userService=new UserService();
                userService.saveUsers(user);
            }
            workbook.close();
            is.close();
            resp.setContentType("text/html;charset=utf-8");
            resp.getWriter().write("导入成功");
        } catch (Exception e) {
            resp.setContentType("text/html;charset=utf-8");
            resp.getWriter().write("导入失败");
            e.printStackTrace();
        }
    }
}
